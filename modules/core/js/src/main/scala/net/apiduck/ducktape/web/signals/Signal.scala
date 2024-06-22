package net.apiduck.ducktape.web.signals

import typings.signalPolyfill.distWrapperMod.Signal as Native

import scala.language.implicitConversions
import scala.scalajs.js
import scala.concurrent.ExecutionContext
import net.apiduck.ducktape.util.unapply.UnapplyFunction
import scala.concurrent.Future
import scala.collection.mutable
import net.apiduck.ducktape.util.unapply.WithUnapplyFunction
import scala.reflect.ClassTag

trait Signal[+T]:

  def get(): T
  def map[R](func: T => R): Signal[R] = Signal.Computed(() => func(get()))

  def subscribe(func: T => UnapplyFunction): UnapplyFunction =
      SignalWatcher.effect(() => func(get()))

  inline def :=> [R](func: T => R): Signal[R] = map(func)

object Signal:

  type MaybeSignal[T] = T | Signal[T]

  private val scalaOptions = js.Dynamic.literal(
    equals = (a: js.Any, b: js.Any) => a == b
  ).asInstanceOf[Native.Options[Any]]

  private[ducktape] inline def options[T]: Native.Options[T] = scalaOptions.asInstanceOf[Native.Options[T]]

  trait WithState[T] extends Signal[T]:
    def set(value: T): Unit
    def modify(func: T => T): Unit = set(func(get()))
    def mapBiDirectional[R](func: T => R, applyChange: (R, T) => T): WithState[R] =
      ComputedWithSource(map(func), value => set(applyChange(value, get())))
    def := (value: T): Unit = set(value)

  class State[T]private(private val signal: Native.State[T]) extends WithState[T]:
    def get(): T = signal.get()
    def set(value: T): Unit = signal.set(value)

  object State:
    def apply[T](initialValue: T): WithState[T] =
      new State(Native.State(initialValue, options))

  class Computed[T]private(private val signal: Native.Computed[T]) extends Signal[T]:
    def get(): T = signal.get()

  object Computed:
    def apply[T](func: () => T): Signal[T] =
      new Computed(Native.Computed(func, options))

  class ComputedWithSource[T](val computed: Signal[T], setter: T => Unit) extends WithState[T]:
    def get(): T = computed.get()
    def set(value: T): Unit = setter(value)

  class ListEntrySignal[T]private[ducktape](val list: WithState[Seq[T]], entrySignal: Signal[T], val getCurrentIndex: () => Int) extends WithState[T]:
    def get(): T = entrySignal.get()
    def set(value: T): Unit = list := list.get().updated(getCurrentIndex(), value)

    def delete(): Unit = list := list.get().patch(getCurrentIndex(), Nil, 1)

    def insertBefore(value: T): Unit = list := list.get().patch(getCurrentIndex(), List(value), 0)
    def insertAfter(value: T): Unit = list := list.get().patch(getCurrentIndex() + 1, List(value), 0)

    def insertTop(value: T): Unit = list := value +: list.get()
    def insertBottom(value: T): Unit = list := list.get() :+ value

  enum SignalLike[+T]:
    case Constant(value: T)
    case WithSignal(signal: Signal[T])

    def get(): T = this match
      case Constant(value) => value
      case WithSignal(signal) => signal.get()

    def toMaybeSignal: MaybeSignal[T] = this match
      case Constant(value) => value
      case WithSignal(signal) => signal

  object SignalLike:
    def apply[T](value: MaybeSignal[T])(using ClassTag[T]): SignalLike[T] = value match
      case t: T => Constant(t)
      case s: Signal[T] @unchecked => WithSignal(s)

  private def mapArgs(args: Seq[Any]): Seq[Any] = args.map:
    case s: Signal[_] => s.get()
    case x => x

  extension (sc: StringContext)

    def si(args: Any*): Signal[String] =
      Computed(() => sc.s(mapArgs(args)*))

    def osi(args: Any*): String | Signal[String] =
      val hasSignal = args.exists(_ match
        case s: Signal[_] => true
        case _ => false
      )
      if hasSignal then
        si(args*)
      else
        sc.s(args*)

    // TODO implement custom interpolators for f string interpolator
    // inline def fi(args: Any*): Computed[String] =
    //   Native.Computed(() => sc.f(mapArgs(args)*))

  extension [T](promise: js.Promise[T]) // TODO handle errors

    def toSignal: Signal[Option[T]] =
      val signal = State[Option[T]](None)
      promise.`then`(value => signal := Some(value))
      signal

    def toOptimisticSignal(optimisticValue: T): Signal[T] =
      val signal = State[T](optimisticValue)
      promise.`then`(value => signal := value)
      signal

  extension [T](future: Future[T]) // TODO handle errors

    def toSignal(using ExecutionContext): Signal[Option[T]] =
      val signal = State[Option[T]](None)
      future.foreach(value => signal := Some(value))
      signal

    def toOptimisticSignal(optimisticValue: T)(using ExecutionContext): Signal[T] =
      val signal = State[T](optimisticValue)
      future.foreach(value => signal := value)
      signal

  private def defaultReduce[T]: (Seq[T] => Seq[T]) | Null = null
  extension [T](signal: WithState[Seq[T]])

    def mapEntries[Key, Created, PostCreate, Return](
      keyFn: T => Key,
      create: (Key, ListEntrySignal[T]) => Created,
      destroy: (Key, Created) => Unit = (_: Key, _: Created) => (),
      postCreate: Created => PostCreate = (c: Created) => c,
      reduce: (Seq[PostCreate] => Return) | Null = defaultReduce[PostCreate]
    ): WithUnapplyFunction[Signal[Return]] =
      val map: mutable.Map[Key, (WithState[T], Created, PostCreate)] = mutable.Map.empty
      var keyIndexes: Seq[Key] = Nil

      def getCurrentIndex(key: Key): Int =
      keyIndexes.indexOf(key)

      val mapped = signal.map: (seq: Seq[T]) =>

        val keyValueMap = seq.map(value => keyFn(value) -> value)
        keyIndexes = keyValueMap.map(_._1)

        // add & update
        keyValueMap.foreach: (key, value) =>
          map.updateWith(key):
            case None =>
              val newSignal = State(value)
              val crated = create(key, ListEntrySignal(signal, newSignal, () => getCurrentIndex(key)))
              val result = postCreate(crated)
              Some((newSignal, crated, result))
            case prev@Some((signal, _, _)) =>
              signal := value
              prev

        // remove
        map.keySet.diff(seq.map(keyFn).toSet)
          .foreach: key =>
            val (_, created, _) = map.remove(key).get
            destroy(key, created)

        // to ordered seq
        keyIndexes.map(key => map(key)._3)

      WithUnapplyFunction(
        if reduce == null then mapped.asInstanceOf[Signal[Return]] else mapped.map(reduce),
        () =>
          map.foreach:
            case (key, (signal, created, _)) =>
              destroy(key, created)
      )

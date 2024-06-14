package net.apiduck.ducktape.web.signals

import typings.signalPolyfill.distWrapperMod.Signal as Native

import scala.language.implicitConversions
import scala.scalajs.js
import scala.concurrent.ExecutionContext
import net.apiduck.ducktape.util.unapply.UnapplyFunction

trait Signal[+T]:

  def get(): T
  def map[R](func: T => R): Signal[R] = Signal.Computed(() => func(get()))

  def subscribe(func: T => UnapplyFunction): UnapplyFunction =
      SignalWatcher.effect(() => func(get()))

  inline def :=> [R](func: T => R): Signal[R] = map(func)

object Signal:

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

  extension [T](future: scala.concurrent.Future[T]) // TODO handle errors

    def toSignal(using ExecutionContext): Signal[Option[T]] =
      val signal = State[Option[T]](None)
      future.foreach(value => signal := Some(value))
      signal

    def toOptimisticSignal(optimisticValue: T)(using ExecutionContext): Signal[T] =
      val signal = State[T](optimisticValue)
      future.foreach(value => signal := value)
      signal

package net.apiduck.ducktape.web.signals

import typings.signalPolyfill.distWrapperMod.Signal as Native

import scala.language.implicitConversions
import scala.scalajs.js

object Signal:

  private val scalaOptions = js.Dynamic.literal(
    equals = (a: js.Any, b: js.Any) => a == b
  ).asInstanceOf[Native.Options[Any]]

  inline def options[T]: Native.Options[T] = scalaOptions.asInstanceOf[Native.Options[T]]

  type Computed[T] = Native.Computed[T]
  type State[T] = Native.State[T]
  type Signal[T] = Signal.State[T] | Signal.Computed[T] | Signal.ComputedWithSource[T]

  def apply[T](initialValue: T): Signal.State[T] =
    // TODO on dev mode the initial value should be ignored and instead the previous value should be used if available (after a hot reload)
    // println(new Exception().getStackTrace()(0).toString().mkString("\n"))
    // if devMode && initialValue == oldInitialValue then
    //   return Native.State(oldLastValue, options)
    Native.State(initialValue, options)

  def apply[T](func: () => T): Signal.Computed[T] =
    Native.Computed(func, options)

  extension [T](signal: Signal[T])

    // common methods

    def get(): T = signal match
      case s: Signal.State[T] => s.get()
      case s: Signal.Computed[T] => s.get()

    // extensions

    def map[R](func: T => R): Signal.Computed[R] =
      Native.Computed(() => func(signal.get()), options)

    def subscribe(func: T => ((() => Unit) | Unit)): () => Unit =
      SignalWatcher.effect(() => func(signal.get()))

    // aliases

    def :=> [R](func: T => R): Signal.Computed[R] =
      map(func)

  extension [T](signal: Signal.State[T])

    // extensions

    def modify (func: T => T): Unit = signal match
      case signal: State[T] => signal.set(func(signal.get()))

    def mapBiDirectional[R](func: T => R, applyChange: (R, T) => T): Signal.ComputedWithSource[R] =
      ComputedWithSource(signal.map(func), value => signal.modify(applyChange(value, _)))

    // aliases

    def := (value: T): Unit = signal match
      case signal: State[T] => signal.set(value)

  // extension [T](signal: Signal.Computed[T])

  private def mapArgs(args: Seq[Any]): Seq[Any] = args.map:
    case s: Signal[_] => s.get()
    case x => x

  extension (sc: StringContext)

    def si(args: Any*): Computed[String] =
      Native.Computed(() => sc.s(mapArgs(args)*), options)

    def osi(args: Any*): String | Computed[String] =
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
      val signal = apply[Option[T]](None)
      promise.`then`(value => signal := Some(value))
      signal

    def toOptimisticSignal(optimisticValue: T): Signal[T] =
      val signal = apply[T](optimisticValue)
      promise.`then`(value => signal := value)
      signal

  class ComputedWithSource[T](private val computed: Signal[T], private val setter: T => Unit) extends State[T]:
    override def get(): T = computed.get()
    override def set(value: T): Unit = setter(value)

  class ListEntrySignal[T](val list: State[Seq[T]], private val computed: Signal[T], val getCurrentIndex: () => Int) extends ComputedWithSource[T](computed, value => list.get().updated(getCurrentIndex(), value)):
    override def set(value: T): Unit = list := list.get().updated(getCurrentIndex(), value)
    def delete(): Unit = list := list.get().patch(getCurrentIndex(), Nil, 1)

    def insertBefore(value: T): Unit = list := list.get().patch(getCurrentIndex(), List(value), 0)
    def insertAfter(value: T): Unit = list := list.get().patch(getCurrentIndex() + 1, List(value), 0)

    def insertTop(value: T): Unit = list := value +: list.get()
    def insertBottom(value: T): Unit = list := list.get() :+ value


  implicit inline def narrow[From, To >: From](from: Signal[From]): Computed[To] =
    from.asInstanceOf[Computed[To]]

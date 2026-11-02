package net.apiduck.ducktape.web.signals

import scala.scalajs.js
import scala.scalajs.js.annotation.*

/** Hand-written facade for the `Signal` export of the `signal-polyfill` npm package
  * (https://github.com/proposal-signals/signal-polyfill). Only the surface DuckTape uses is declared.
  */
private[ducktape] object SignalPolyfill:

  type AnySignal = Signal.State[?] | Signal.Computed[?]

  trait Options[T] extends js.Object:
    var equals: js.UndefOr[js.Function2[T, T, Boolean]] = js.undefined

  @js.native
  @JSImport("signal-polyfill", "Signal")
  object Signal extends js.Object:

    @js.native
    class State[T](initialValue: T, options: js.UndefOr[Options[T]] = js.native) extends js.Object:
      def get(): T = js.native
      def set(newValue: T): Unit = js.native

    @js.native
    class Computed[T](computation: js.Function0[T], options: js.UndefOr[Options[T]] = js.native) extends js.Object:
      def get(): T = js.native

    @js.native
    object subtle extends js.Object:

      @js.native
      class Watcher(@annotation.unused callback: js.ThisFunction0[Watcher, Unit]) extends js.Object:
        def watch(signals: AnySignal*): Unit = js.native
        def unwatch(signals: AnySignal*): Unit = js.native
        def getPending(): js.Array[Computed[Any]] = js.native

      def untrack[T](cb: js.Function0[T]): T = js.native
      def currentComputed(): js.UndefOr[Computed[Any]] = js.native

package net.apiduck.ducktape.web.signals

import typings.signalPolyfill.distWrapperMod.Signal as Native
import typings.signalPolyfill.distWrapperMod.Signal.subtle.Watcher

import scala.concurrent.Future
import scala.scalajs.js.JSConverters.*
import net.apiduck.ducktape.util.unapply.*

private[ducktape] object SignalWatcher:

  private var needsProcessing = true

  private val w = new Watcher(_ => {
    if needsProcessing then
      needsProcessing = false
      // TODO this execution context is using the micro-task queue, not the macro-task queue, that can cause unexpected behavior
      import scala.concurrent.ExecutionContext.Implicits.global
      Future:
        processPending()
  })

  private def processPending(): Unit =
    needsProcessing = true

    for s <- w.getPending() do
      s.get()

    if w.getPending().nonEmpty then
			// this might indicate a bug the application (and most likely in a bug in the Signal.mapEntries & Signal.mapEntriesWithIndex implementations)
			// a good implementation for the scheduling of the watcher might be to schedule the whatcher in the micro task queue and this rerun in the macro task queue (if it is not scheduled on the micro task queue before)
      needsProcessing = false
      // TODO this execution context is using the micro-task queue, not the macro-task queue, that can cause unexpected behavior
      import scala.concurrent.ExecutionContext.Implicits.global
      Future:
        processPending()

    w.watch()

  private[signals] def effect(callback: () => UnapplyFunction): () => Unit =
    var unapply: UnapplyFunction = ()

    val computed: Native.Computed[Any] = Native.Computed(() => {
      unapply()
      unapply = callback()
    }, Signal.options)

    w.watch(computed)
    computed.get()

    () => {
      w.unwatch(computed)
      unapply()
    }

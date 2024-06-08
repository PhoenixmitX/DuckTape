package net.apiduck.ducktape.web.signals


import net.apiduck.ducktape.web.signals.Signal.Computed
import org.scalajs.dom.*
import typings.signalPolyfill.distWrapperMod.Signal as Native
import typings.signalPolyfill.distWrapperMod.Signal.subtle.Watcher

import scala.scalajs.js.JSConverters.*

private[ducktape] object SignalWatcher:

  private var needsProcessing = true

  private val w = new Watcher(_ => {
    if needsProcessing then
      needsProcessing = false
      window.setTimeout(() => processPending(), 0)
  })

  private def processPending(): Unit =
    needsProcessing = true

    for s <- w.getPending() do
      s.get()

    w.watch()

  def effect(callback: () => ((() => Unit) | Unit)): () => Unit =
    var cleanup: (() => Unit) | Unit = ()

    val computed: Native.Computed[Any] = Native.Computed(() => {
      if cleanup.isInstanceOf[() => Unit] then cleanup.asInstanceOf[() => Unit]()
      cleanup = callback()
    }, Signal.options)

    w.watch(computed)
    computed.get()

    () => {
      w.unwatch(computed)
      if cleanup != () then cleanup.asInstanceOf[() => Unit]()
    }

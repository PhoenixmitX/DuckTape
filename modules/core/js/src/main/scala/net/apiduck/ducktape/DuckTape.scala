package net.apiduck.ducktape

import org.scalajs.dom.*
import net.apiduck.ducktape.DT.*
import net.apiduck.ducktape.util.unapply.*
import net.apiduck.ducktape.web.signals.Signal
import net.apiduck.ducktape.web.signals.Signal.SignalLike

object DuckTape {

  def getOrCreateElement(id: String = "root"): Element =
    Option(document.getElementById(id)).getOrElse:
      val elem = document.createElement("div")
      elem.id = id
      document.body.appendChild(elem)
      elem

  def render(id: String = "root", component: DT.DTX): UnapplyFunction =
    val root = getOrCreateElement(id)
    val WithUnapplyFunction(elements, unapply) = component.render()
    SignalLike(elements) match
      case SignalLike.WithSignal(signal) =>
        val unapplySignal = signal.subscribe: nodes =>
          root.innerHTML = ""
          nodes.foreach(root.appendChild)
        () => {
          unapply()
          unapplySignal()
          root.innerHTML = ""
        }
      case SignalLike.Constant(nodes) =>
        nodes.foreach(element => root.appendChild(element))
        () => {
          unapply()
          root.innerHTML = ""
        }
}

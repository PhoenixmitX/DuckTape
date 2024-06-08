package net.apiduck.ducktape

import org.scalajs.dom.*
import net.apiduck.ducktape.DT.*
import net.apiduck.ducktape.util.unapply.*

object DuckTape {

  def getOrCreateElement(id: String = "root"): Element =
    Option(document.getElementById(id)).getOrElse:
      val elem = document.createElement("div")
      elem.id = id
      document.body.appendChild(elem)
      elem

  def render(renderable: DT.DTX, id: String = "root"): UnapplyFunction =
    val root = getOrCreateElement(id)
    val (element, unapply) = renderable.render()
    root.appendChild(element)
    () => {
      unapply()
      root.removeChild(element)
    }
}

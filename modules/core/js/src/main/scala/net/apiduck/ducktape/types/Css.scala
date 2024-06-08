package net.apiduck.ducktape.types

import net.apiduck.ducktape.compatibility.ElementType
import scala.Predef.String as SString

enum Css:
  case String(name: SString, value: SString)
  case Number(name: SString, value: Double)

  def applyTo(element: ElementType.Element): Unit =
    this match
      case Css.String(name, value) => element.style.setProperty(name, value)
      case Css.Number(name, value) => element.style.setProperty(name, value.toString)

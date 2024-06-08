package net.apiduck.ducktape.types

import net.apiduck.ducktape.compatibility.ElementType

import scala.Boolean as SBoolean
import scala.Predef.String as SString
import net.apiduck.ducktape.compatibility.EventType

trait AnyAttribute[-E <: ElementType.Element]:
  def renderToHTMLString(): String

enum Attribute[-E <: ElementType.Element] extends AnyAttribute[E]:
  case String(name: SString, value: SString)
  case Number(name: SString, value: Double)
  case Boolean(name: SString, value: SBoolean)
  case Style(style: Css*)
  case Event(name: SString, value: EventType.Event => Unit)

  def renderToHTMLString(): SString =
    this match
      case Attribute.String(name, value) => s"""$name="$value"""" // TODO escape value
      case Attribute.Number(name, value) => s"""$name="$value""""
      case Attribute.Boolean(name, value) => if value then name else ""
      case Attribute.Style(styles @ _*) => s"""style="${styles.map(_.renderToHTMLString()).mkString(";")}""""
      case Attribute.Event(name, _) => "" // TODO implement

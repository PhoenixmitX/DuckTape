package net.apiduck.ducktape.types

import net.apiduck.ducktape.compatibility.ElementType

import scala.Boolean as SBoolean
import scala.Predef.String as SString
import net.apiduck.ducktape.compatibility.EventType
import net.apiduck.ducktape.web.signals.Signal.Signal
import net.apiduck.ducktape.util.unapply.*

trait AnyAttribute[-E <: ElementType.Element]:
  def applyTo(element: ElementType.Element): UnapplyFunction

enum Attribute[-E <: ElementType.Element] extends AnyAttribute[E]:
  case String(name: SString, value: SString)
  case Number(name: SString, value: Double)
  case Boolean(name: SString, value: SBoolean)
  case Style(style: Css*)
  case Event(name: SString, value: EventType.Event => Unit)

  def applyTo(element: ElementType.Element): Unit  =
    this match
      case Attribute.String(name, value) => element.setAttribute(name, value)
      case Attribute.Number(name, value) => element.setAttribute(name, value.toString)
      case Attribute.Boolean(name, value) => if value then element.setAttribute(name, "")
      case Attribute.Style(styles @ _*) => styles.foreach(_.applyTo(element))
      case Attribute.Event(name, value) => element.addEventListener(name, value)


enum SignalAttribute[-E <: ElementType.Element] extends AnyAttribute[E]:
  case String(name: SString, value: Signal[SString])
  case Number(name: SString, value: Signal[Double])
  case Boolean(name: SString, value: Signal[SBoolean])
  case Style(style: Signal[Seq[Css] | SString])
  case Event(name: SString, value: Signal[EventType.Event => Unit])

  def applyTo(element: ElementType.Element): UnapplyFunction =
    this match
      case SignalAttribute.String(name, value) => value.subscribe(element.setAttribute(name, _))
      case SignalAttribute.Number(name, value) => value.subscribe(v => element.setAttribute(name, v.toString))
      case SignalAttribute.Boolean(name, value) => value.subscribe(v => if v then element.setAttribute(name, "") else element.removeAttribute(name))
      case SignalAttribute.Style(value) => value.subscribe: styles =>
        if styles.isInstanceOf[SString] then element.setAttribute("style", styles.asInstanceOf[SString])
        else styles.asInstanceOf[Seq[Css]].foreach(_.applyTo(element))
      case SignalAttribute.Event(name, value) => value.subscribe(v => element.addEventListener(name, v)) // TODO unsubscribe previous listener

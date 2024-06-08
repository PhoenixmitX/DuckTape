package net.apiduck.ducktape.types

import net.apiduck.ducktape.compatibility.ElementType

import scala.Boolean as SBoolean
import scala.Predef.String as SString
import net.apiduck.ducktape.compatibility.EventType
import net.apiduck.ducktape.web.signals.Signal.Signal

trait AnyAttributeType[-E <: ElementType.Element]

enum AttributeType[-E <: ElementType.Element, T] extends AnyAttributeType[E]:
  case String[-E <: ElementType.Element](name: SString) extends AttributeType[E, SString]
  case Number[-E <: ElementType.Element](name: SString) extends AttributeType[E, Double]
  case Boolean[-E <: ElementType.Element](name: SString) extends AttributeType[E, SBoolean]
  case Style extends AttributeType[ElementType.Element, Seq[Css] | SString]
  case Event[-E <: ElementType.Element, V <: EventType.Event](name: SString) extends AttributeType[E, V => Unit]

  def := (value: T): Attribute[E] =
    this match
      case String(name) => Attribute.String(name, value.asInstanceOf[SString])
      case Number(name) => Attribute.Number(name, value.asInstanceOf[Double])
      case Boolean(name) => Attribute.Boolean(name, value.asInstanceOf[SBoolean])
      case Style =>
        if value.isInstanceOf[SString] then Attribute.String("style", value.asInstanceOf[SString])
        else Attribute.Style(value.asInstanceOf[Seq[Css]]*)
      case Event(name) => Attribute.Event(name, value.asInstanceOf[EventType.Event => Unit])

  def := (value: Signal[T]): SignalAttribute[E] =
    this match
      case String(name) => SignalAttribute.String(name, value.asInstanceOf[Signal[SString]])
      case Number(name) => SignalAttribute.Number(name, value.asInstanceOf[Signal[Double]])
      case Boolean(name) => SignalAttribute.Boolean(name, value.asInstanceOf[Signal[SBoolean]])
      case Style => SignalAttribute.Style(value.asInstanceOf[Signal[Seq[Css] | SString]])
      case Event(name) => SignalAttribute.Event(name, value.asInstanceOf[Signal[EventType.Event => Unit]])

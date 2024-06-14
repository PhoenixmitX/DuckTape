package net.apiduck.ducktape

import net.apiduck.ducktape.compatibility.ElementType

import org.scalajs.dom.*
import net.apiduck.ducktape.web.signals.Signal
import net.apiduck.ducktape.util.unapply.*

import scala.language.implicitConversions
import net.apiduck.ducktape.types.AnyAttribute

enum DT extends DT.DTX:
  import DT.*
  case Tag[E <: ElementType.Element](tag: String, needsClosingTag: Boolean)(val attributes: AnyAttribute[E]*)(val children: DT.DTX*)
  case Text(text: String)
  case SignalText(signal: Signal[String])
  case Empty

  override def render(): WithUnapplyFunction[Seq[Node]] =
    this match
      case dt: Tag[?] =>
        val element = document.createElement(dt.tag).asInstanceOf[ElementType.Element]
        val unapplyAttributes: Seq[UnapplyFunction] = dt.attributes.map(_.applyTo(element))
        val unapplyChildren = dt.children.map(_.render()).foreachAndGetUnapplyFunction(_.foreach(element.appendChild))
        (
          Seq(element),
          () =>
            unapplyAttributes.foreach(_())
            unapplyChildren()
        )
      case Text(text) =>
        (Seq(document.createTextNode(text)), ())
      case SignalText(signal) =>
        val element = document.createElement("span").asInstanceOf[ElementType.Element]
        val unsubscribe = signal.subscribe(v => element.textContent = v)
        (Seq(element), unsubscribe)
      case Empty =>
        (Nil, ())

object DT:

  trait DTX:
    def render(): WithUnapplyFunction[Seq[Node]]

  def apply[RenderType <: Node](createFunc: () => RenderType): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[Seq[RenderType]] = (Seq(createFunc()), ())

  def withUnapplyFunction[RenderType <: Node](renderFunc: () => WithUnapplyFunction[Seq[RenderType]]): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[Seq[RenderType]] =
        renderFunc()

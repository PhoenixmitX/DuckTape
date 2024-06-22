package net.apiduck.ducktape

import net.apiduck.ducktape.compatibility.ElementType
import net.apiduck.ducktape.types.AnyAttribute
import net.apiduck.ducktape.util.unapply.*
import net.apiduck.ducktape.web.signals.Signal
import org.scalajs.dom.*

import scala.language.implicitConversions
import net.apiduck.ducktape.web.signals.Signal.MaybeSignal
import net.apiduck.ducktape.web.signals.Signal.SignalLike
import scala.reflect.ClassTag

enum DT extends DT.DTX:
  import DT.*
  case Tag[E <: ElementType.Element](tag: String, needsClosingTag: Boolean = false)(val attributes: AnyAttribute[E]*)(val children: DT.Children*)
  case Text(text: String)
  case SignalText(signal: Signal[String])
  case Empty
  case Fragment(children: DT.DTX*)

  override def render(): WithUnapplyFunction[MaybeSignal[Seq[Node]]] =
    this match
      case dt: Tag[?] =>
        val mappedChildren = dt.children.map(_.render())

        val nodes = Signal.Computed(() => mappedChildren.flatMap(c => SignalLike(c.value).get()))

        val element = document.createElement(dt.tag).asInstanceOf[ElementType.Element]
        val unapplyAttributes: Seq[UnapplyFunction] = dt.attributes.map(_.applyTo(element))

        val unapplyNodes = nodes.subscribe: nodes =>
          element.innerHTML = ""
          nodes.foreach(element.appendChild)

        val unapplyChildren = () => mappedChildren.foreach(_.unapply())

        WithUnapplyFunction(
          Seq(element),
          () =>
            unapplyAttributes.foreach(_())
            unapplyNodes()
            unapplyChildren()
        )
      case Text(text) =>
        WithUnapplyFunction(Seq(document.createTextNode(text)))
      case SignalText(signal) =>
        val element = document.createTextNode("")
        val unsubscribe = signal.subscribe(v => element.textContent = v)
        WithUnapplyFunction(Seq(element), unsubscribe)
      case Empty =>
        WithUnapplyFunction(Nil)
      case Fragment(children*) =>
        val mappedChildren = children.map(_.render())
        val nodes = Signal.Computed(() => mappedChildren.flatMap(c => SignalLike(c.value).get()))
        val unapplyFunctions = mappedChildren.map(_._2)
        WithUnapplyFunction(
          nodes,
          () => unapplyFunctions.foreach(_())
        )

object DT:

  type Children = DT.DTX

  trait DTX:
    def render(): WithUnapplyFunction[MaybeSignal[Seq[Node]]]

  def apply[RenderType <: Node](createFunc: () => RenderType): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[Seq[RenderType]] = WithUnapplyFunction(Seq(createFunc()), ())

  def withUnapplyFunction[RenderType <: Node](renderFunc: () => WithUnapplyFunction[Seq[RenderType]]): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[Seq[RenderType]] =
        renderFunc()

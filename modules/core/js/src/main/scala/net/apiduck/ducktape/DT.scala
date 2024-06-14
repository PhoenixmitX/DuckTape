package net.apiduck.ducktape

import net.apiduck.ducktape.compatibility.ElementType

import org.scalajs.dom.*
import net.apiduck.ducktape.web.signals.Signal
import net.apiduck.ducktape.util.unapply.*

import scala.language.implicitConversions
import net.apiduck.ducktape.types.AnyAttribute

enum DT extends DT.DTX:
  import DT.*
  case Tag[E <: ElementType.Element](tag: String, needsClosingTag: Boolean = false)(val attributes: AnyAttribute[E]*)(val children: DT.TagChildren*)
  case Text(text: String)
  case SignalText(signal: Signal[String])
  case Empty
  case Fragment(children: DT.DTX*)

  override def render(): WithUnapplyFunction[Seq[Node]] =
    this match
      case dt: Tag[?] =>
        val dtxElements = dt.children.map:
          case child: DT.DTX =>
            val (nodes, unapply) = child.render()
            DTXElements.CachedElement(nodes, unapply)
          case (child: Signal[Seq[Node]], unapply) =>
            DTXElements.SignalElement(child, unapply)

        val nodes = Signal.Computed(() => dtxElements.flatMap(_.getCurrent()))

        val element = document.createElement(dt.tag).asInstanceOf[ElementType.Element]
        val unapplyAttributes: Seq[UnapplyFunction] = dt.attributes.map(_.applyTo(element))

        val unapplyNodes = nodes.subscribe: nodes =>
          element.innerHTML = "" // TODO can this be optimized?
          nodes.foreach(element.appendChild)

        val unapplyChildren = () => dtxElements.foreach(_.unapply())

        (
          Seq(element),
          () =>
            unapplyAttributes.foreach(_())
            unapplyNodes()
            unapplyChildren()
        )
      case Text(text) =>
        (Seq(document.createTextNode(text)), ())
      case SignalText(signal) =>
        val element = document.createTextNode("")
        val unsubscribe = signal.subscribe(v => element.textContent = v)
        (Seq(element), unsubscribe)
      case Empty =>
        (Nil, ())
      case Fragment(children*) =>
        val mappedChildren = children.map(_.render())
        val elements = mappedChildren.flatMap(_._1)
        val unapplyFunctions = mappedChildren.map(_._2)
        (
          elements,
          () => unapplyFunctions.foreach(_())
        )

object DT:

  type TagChildren = DT.DTX | WithUnapplyFunction[Signal[Seq[Node]]]

  trait DTX:
    def render(): WithUnapplyFunction[Seq[Node]]

  def apply[RenderType <: Node](createFunc: () => RenderType): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[Seq[RenderType]] = (Seq(createFunc()), ())

  def withUnapplyFunction[RenderType <: Node](renderFunc: () => WithUnapplyFunction[Seq[RenderType]]): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[Seq[RenderType]] =
        renderFunc()


  private enum DTXElements:
    case CachedElement(element: Seq[Node], unapplyFunction: UnapplyFunction)
    case SignalElement(signal: Signal[Seq[Node]], unapplyFunction: UnapplyFunction)

    def getCurrent(): Seq[Node] = this match
      case CachedElement(element, _) => element
      case SignalElement(signal, _) => signal.get()

    def unapply(): UnapplyFunction = this match
      case CachedElement(_, unapplyFunction) => unapplyFunction
      case SignalElement(_, unapplyFunction) => unapplyFunction()


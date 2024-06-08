package net.apiduck.ducktape.render

import net.apiduck.ducktape.render.native.NativeAttributes
import net.apiduck.ducktape.render.native.NativeCSS
import net.apiduck.ducktape.render.native.NativeHtmlElements
import net.apiduck.ducktape.render.native.SignalRenderer
import net.apiduck.ducktape.render.types.Renderable
import net.apiduck.ducktape.render.types.RenderableType
import net.apiduck.ducktape.signals.Signal
import net.apiduck.ducktape.signals.Signal.*
import org.scalajs.dom.*
import org.scalajs.dom.Node

import scala.language.implicitConversions

import types.AttributeWithValue

object Renderer
  extends NativeAttributes
  with NativeHtmlElements
  with SignalRenderer:

  object css extends NativeCSS
  type NativeType = String | Double | Float | Long | Int | Char | Short | Byte | Boolean

  implicit def renderNativeType(value: NativeType): Renderable[Text] =
    Renderable[Text](() => document.createTextNode(value.toString()))

  implicit def renderNativeTypeSignal[T <: NativeType](value: Signal[T]): Renderable[Text] =
    Renderable.withUnapplyFunction[Text](() =>
      val node = document.createTextNode(value.get().toString())
      val unsubscribe = value.subscribe(newValue => node.textContent = newValue.toString())
      (node, unsubscribe)
    )

  def renderTag[E <: HTMLElement](tag: String)(attributes: AttributeWithValue[E]*)(children: Renderable[RenderableType]*): Renderable[E] = // TODO allow advanced types
    new Renderable[E]:

      var unsubscribeAttributes: Seq[() => Unit] = Nil

      override def create(): E =
        val node = document.createElement(tag).asInstanceOf[E]
        unsubscribeAttributes = attributes.map(_.apply(node))
        children.map(_.create())
          .foreach(node.appendChild)
        node

      override def destroy(): Unit =
        children.foreach(_.destroy())
        unsubscribeAttributes.foreach(_())

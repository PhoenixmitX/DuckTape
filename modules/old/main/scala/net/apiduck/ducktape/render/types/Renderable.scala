package net.apiduck.ducktape.render.types

import org.scalajs.dom.*

type RenderableType = Node

type AdvancedRenderableType = RenderableType | Seq[RenderableType]

type UnapplyFunction = (() => Unit) | Unit
extension (uf: UnapplyFunction)
  inline def apply(): Unit =
    uf.map(_())

trait Renderable[+RenderType <: AdvancedRenderableType]:
  def create(): RenderType
  def destroy(): Unit = ()

object Renderable:
  def apply[RenderType <: AdvancedRenderableType](createFunc: () => RenderType): Renderable[RenderType] =
    new Renderable[RenderType]:
      override def create(): RenderType = createFunc()

  def withUnapplyFunction[RenderType <: AdvancedRenderableType](createFunc: () => (RenderType, UnapplyFunction)): Renderable[RenderType] =
    new Renderable[RenderType]:
      var unapplyFunction: UnapplyFunction = ()
      override def create(): RenderType =
        val (renderType, unapply) = createFunc()
        unapplyFunction = unapply
        renderType
      override def destroy(): Unit = unapplyFunction()

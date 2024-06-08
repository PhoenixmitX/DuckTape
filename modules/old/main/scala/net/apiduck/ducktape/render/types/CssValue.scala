package net.apiduck.ducktape.render.types

import net.apiduck.ducktape.signals.Signal
import net.apiduck.ducktape.signals.Signal.*
import org.scalajs.dom.*

type CssValueWithValue = (element: CSSStyleDeclaration) => () => Unit

// FIXME CssValues have a design flaw, they cant be removed
trait CssValue[T]:
  def set(element: CSSStyleDeclaration)(value: T): Unit
  def remove(element: CSSStyleDeclaration): Unit
  def set(element: CSSStyleDeclaration)(value: Signal[T]): () => Unit =
    val unsubscribe = value.subscribe:
      set(element)(_)
    () => unsubscribe()

  def := (value: T): CssValueWithValue = (element: CSSStyleDeclaration) =>
    set(element)(value)
    () => ()

  def := (value: Signal[T]): CssValueWithValue =
    set(_)(value)

object CssValue:

  def apply(name: String): CssValue[String] = new CssValue[String]:
    def set(element: CSSStyleDeclaration)(value: String): Unit =
      element.setProperty(name, value)
    def remove(element: CSSStyleDeclaration): Unit =
      element.removeProperty(name)


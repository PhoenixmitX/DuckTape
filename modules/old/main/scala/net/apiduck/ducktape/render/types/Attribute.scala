package net.apiduck.ducktape.render.types

import net.apiduck.ducktape.signals.Signal
import net.apiduck.ducktape.signals.Signal.*
import org.scalajs.dom.*

type AttributeWithValue[E <: HTMLElement] = (element: E) => () => Unit

// FIXME attributes have a design flaw, they cant be removed
trait Attribute[T, E <: HTMLElement]:
  def set(element: E)(value: T): Unit
  def remove(element: E): Unit
  def set(element: E)(value: Signal[T]): () => Unit =
    val unsubscribe = value.subscribe:
      set(element)(_)
    () => unsubscribe()

  def := (value: T): AttributeWithValue[E] = (element: E) =>
    set(element)(value)
    () => ()

  def := (value: Signal[T]): AttributeWithValue[E] =
    set(_)(value)

object Attribute:

  def apply[T, E <: HTMLElement](setFunc: (E, T) => Unit, removeFunc: E => Unit): Attribute[T, E] = new Attribute[T, E]:
    def set(element: E)(value: T): Unit =
      setFunc(element, value)
    def remove(element: E): Unit =
      removeFunc(element)

  def string[E <: HTMLElement](name: String): Attribute[String, E] = new Attribute[String, E]:
    def set(element: E)(value: String): Unit =
      element.setAttribute(name, value)
    def remove(element: E): Unit =
      element.removeAttribute(name)

  def boolean[E <: HTMLElement](name: String): Attribute[Boolean, E] = new Attribute[Boolean, E]:
    def set(element: E)(value: Boolean): Unit =
      if value then
        element.setAttribute(name, "")
      else
        element.removeAttribute(name)
    def remove(element: E): Unit =
      element.removeAttribute(name)

  def number[E <: HTMLElement](name: String): Attribute[Int | Long | Byte | Short | Double | Float, E] = new Attribute[Int | Long | Byte | Short | Double | Float, E]:
    def set(element: E)(value: Int | Long | Byte | Short | Double | Float): Unit =
      element.setAttribute(name, value.toString)
    def remove(element: E): Unit =
      element.removeAttribute(name)

  def event[E <: HTMLElement](name: String): Attribute[Event => Unit, E] = new Attribute[Event => Unit, E]:
    def set(element: E)(value: Event => Unit): Unit =
      element.addEventListener(name, value)
    def remove(element: E): Unit =
      element.removeEventListener(name, null)


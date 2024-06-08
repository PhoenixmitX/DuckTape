package net.apiduck.ducktape.util

import scala.scalajs.js.UndefOr

object unapply:
  type UnapplyFunction = UndefOr[() => Unit]
  type WithUnapplyFunction[T] = (T, UnapplyFunction)

  extension [T](t: T)
    inline def withUnapplyFunction(unapply: () => Unit): WithUnapplyFunction[T] = (t, unapply)
    inline def withoutUnapplyFunction: WithUnapplyFunction[T] = (t, ())

  extension (uf: UnapplyFunction)
    inline def apply(): Unit =
      uf.map(_())

  extension [T](wuf: Seq[WithUnapplyFunction[T]])
    def foreachAndGetUnapplyFunction(forEach: T => Unit): UnapplyFunction =
      val allUnapplyFunctions = wuf.map { case (t, unapply) => forEach(t); unapply }
      () => allUnapplyFunctions.foreach(_())

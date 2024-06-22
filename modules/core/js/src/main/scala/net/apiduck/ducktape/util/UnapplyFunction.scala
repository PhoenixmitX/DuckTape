package net.apiduck.ducktape.util

import scala.scalajs.js.UndefOr
import scala.language.implicitConversions

object unapply:
  type UnapplyFunction = UndefOr[() => Unit]

  extension (uf: UnapplyFunction)
    inline def apply(): Unit =
      uf.map(_())

  case class WithUnapplyFunction[+T](value: T, unapply: UnapplyFunction = ()):
    def map[R](f: T => R): WithUnapplyFunction[R] = WithUnapplyFunction(f(value), unapply)
    def flatMap[R](f: T => WithUnapplyFunction[R]): WithUnapplyFunction[R] =
      val WithUnapplyFunction(r, nextUnapply) = f(value)
      WithUnapplyFunction(r, () => { unapply(); nextUnapply() })

    def hasUnapply: Boolean = unapply.isDefined


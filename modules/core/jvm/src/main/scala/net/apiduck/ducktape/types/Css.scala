package net.apiduck.ducktape.types

import scala.Predef.String as SString

enum Css:
  case String(name: SString, value: SString)
  case Number(name: SString, value: Double)

  def renderToHTMLString(): SString =
    this match
      case Css.String(name, value) => s"""$name: $value""" // TODO escape value
      case Css.Number(name, value) => s"""$name: $value"""

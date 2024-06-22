package net.apiduck.ducktape.types

import net.apiduck.ducktape.util.Escape

import scala.Predef.String as SString

enum Css:
  case String(name: SString, value: SString)
  case Number(name: SString, value: Double)

  def renderToHTMLString(): SString =
    this match
      case Css.String(name, value) => s"$name: ${Escape.escapeSingleQuotedString(value)}"
      case Css.Number(name, value) => s"$name: $value"

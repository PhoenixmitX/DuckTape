package net.apiduck.ducktape

import net.apiduck.ducktape.compatibility.ElementType
import net.apiduck.ducktape.compatibility.ElementType.Element
import net.apiduck.ducktape.util.Escape

import types.AnyAttribute

enum DT extends DT.DTX:
  case Tag[E <: Element](tag: String, needsClosingTag: Boolean)(val attributes: AnyAttribute[E]*)(val children: DT.Children*)
  case Text(text: String)
  case Empty
  case Fragment(children: DT.DTX*)

  def renderToHTMLString(): String =
    this match
      case dt: Tag[?] =>
        if dt.needsClosingTag then
          val attributes = dt.attributes.map(_.renderToHTMLString()).mkString
          val children = dt.children.map(_.renderToHTMLString()).mkString
          s"<${dt.tag} $attributes>$children</${dt.tag}>"
        else
          val attributes = dt.attributes.map(_.renderToHTMLString()).mkString
          s"<${dt.tag} $attributes>"
      case Text(text) => Escape.escapeHTMLString(text)
      case Empty => ""
      case Fragment(children*) => children.map(_.renderToHTMLString()).mkString

object DT:

  type Children = DT.DTX

  trait DTX:
    def renderToHTMLString(): String

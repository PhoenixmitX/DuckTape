package net.apiduck.ducktape.util

/**
 * Utility object for escaping strings.
 * Used for escaping strings in HTML.
 */
object Escape:

  // TODO what needs to be escaped? Do we need to escape newlines?
  def escapeDoubleQuotedString(s: String): String =
    s.flatMap:
      case '"' => "\\\""
      case '\\' => "\\\\"
      case c => c.toString

  def toDoubleQuotedString(s: String): String =
    s""""${escapeDoubleQuotedString(s)}""""

  // TODO what needs to be escaped? Do we need to escape newlines?
  def escapeSingleQuotedString(s: String): String =
    s.flatMap:
      case '\'' => "\\'"
      case '\\' => "\\\\"
      case c => c.toString

  def toSingleQuotedString(s: String): String =
    s"'${escapeSingleQuotedString(s)}'"

  def escapeHTMLString(s: String, escapeWhitespace: Boolean = false, escapeNewlines: Boolean = false): String =
    s.flatMap:
      case '&' => "&amp;"
      case '<' => "&lt;"
      case '>' => "&gt;"
      case '"' => "&quot;"
      case '\'' => "&#39;"
      case ' ' if escapeWhitespace => "&nbsp;" // TODO only escape leading and trailing whitespace?
      case '\n' if escapeNewlines => "<br>"
      case c => c.toString

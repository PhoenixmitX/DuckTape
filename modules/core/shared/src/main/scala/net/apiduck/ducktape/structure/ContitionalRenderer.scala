package net.apiduck.ducktape.structure

import net.apiduck.ducktape.DT

trait ConditionalRenderer:

  def If(condition: Boolean)(`then`: => DT.DTX): DT.DTX =
    If(condition)(`then`)()

  def If(condition: Boolean)(`then`: => DT.DTX)(`else`: => DT.DTX = DT.Empty): DT.DTX =
    if condition then `then` else `else`

  def Match[E](value: E)(cases: PartialFunction[E, DT.DTX]): DT.DTX =
    cases.applyOrElse(value, _ => DT.Empty)

  def ForEach[E](in: IterableOnce[E])(render: E => DT.DTX): DT.DTX =
    DT.Fragment(in.iterator.toSeq.map(render)*)

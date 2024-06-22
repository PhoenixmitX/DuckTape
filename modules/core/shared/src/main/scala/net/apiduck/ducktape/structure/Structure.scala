package net.apiduck.ducktape.structure

import net.apiduck.ducktape.DT
import net.apiduck.ducktape.render.native.NativeAttributes

import scala.language.implicitConversions

object Structure
  extends PlatformDependentStructure
  with Tags
  with NativeAttributes
  with ConditionalRenderer:

  implicit def stringToDt(s: String): DT.Text = DT.Text(s)

  type NativeType = Double | Float | Long | Int | Char | Short | Byte | Boolean

  implicit def nativeToDt[T <: NativeType](value: T): DT.Text = DT.Text(value.toString)

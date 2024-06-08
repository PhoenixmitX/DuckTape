package net.apiduck.ducktape.structure

import net.apiduck.ducktape.DT
import net.apiduck.ducktape.render.native.NativeAttributes

import scala.language.implicitConversions

object Structure
  extends PlatformDependentStructure
  with Tags
  with NativeAttributes:

  implicit def stringToDT(s: String): DT.Text = DT.Text(s)

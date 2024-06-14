package net.apiduck.ducktape.structure

import net.apiduck.ducktape.DT
import net.apiduck.ducktape.DT.*
import net.apiduck.ducktape.web.signals.Signal
import net.apiduck.ducktape.web.signals.Signal.*

import scala.language.implicitConversions

trait SignalRenderer:

  implicit def textSignalToDt(signal: Signal[String]): DT.SignalText =
    DT.SignalText(signal)

  type NativeType = Double | Float | Long | Int | Char | Short | Byte | Boolean

  implicit def nativeSignalToDt[T <: NativeType](signal: Signal[T]): DT.SignalText =
    DT.SignalText(si"$signal")

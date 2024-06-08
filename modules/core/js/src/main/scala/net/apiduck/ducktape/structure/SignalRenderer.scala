package net.apiduck.ducktape.structure

import net.apiduck.ducktape.DT
import net.apiduck.ducktape.DT.*
import net.apiduck.ducktape.web.signals.Signal
import net.apiduck.ducktape.web.signals.Signal.*
import net.apiduck.ducktape.web.signals.Signal.ListEntrySignal
import net.apiduck.ducktape.web.signals.Signal.State
import org.scalajs.dom.*

import scala.collection.mutable
import scala.language.implicitConversions
import net.apiduck.ducktape.util.unapply.*

trait SignalRenderer:

  implicit def textSignalToDt(signal: Signal[String]): DT.SignalText =
    DT.SignalText(signal)

  type NativeType = Double | Float | Long | Int | Char | Short | Byte | Boolean

  implicit def nativeSignalToDt[T <: NativeType](signal: Signal[T]): DT.SignalText =
    DT.SignalText(si"$signal")

  extension [T](signal: State[Seq[T]])

    def :=>> (renderFunc: ListEntrySignal[T] => DT.DTX)(using keyFunc: T => String): DT.DTX = // TODO rewrite using Renderable.withUnapplyFunction
      DT.withUnapplyFunction[HTMLElement]: () =>
        val fragment = document.createElement("f").asInstanceOf[HTMLElement] // TODO custom component with "display: contents"
        val map = mutable.Map.empty[String, (State[T], DT.DTX, (Node, UnapplyFunction))]
        var keyIndexes: Seq[String] = Nil

        def getCurrentIndex(key: String): Int =
          keyIndexes.indexOf(key)

        def update(seq: Seq[T]): Unit =
          val keyValueMap = seq.map(value => keyFunc(value) -> value)
          keyIndexes = keyValueMap.map(_._1)

          // add & update
          keyValueMap.foreach: (key, value) =>
            map.updateWith(key):
              case None =>
                val newSignal = Signal(value)
                val renderable = renderFunc(ListEntrySignal(signal, newSignal, () => getCurrentIndex(key)))
                Some((newSignal, renderable, renderable.render()))
              case prev@Some((signal, _, _)) =>
                signal := value
                prev

          // remove
          map.keySet.diff(seq.map(keyFunc).toSet)
            .foreach: key =>
              val (_, renderable, (element, unapply)) = map.remove(key).get
              fragment.removeChild(element)
              unapply()

          // ordering
          keyIndexes.foreach: key =>
            val (_, _, (element, _)) = map(key)
            fragment.appendChild(element)


        val unsubscribe = signal.subscribe:
          update(_) // TODO can this be resolved with an effect?

        (
          fragment,
          () =>
            unsubscribe()
            map.foreach:
              case (_, (_, _, (_, unapply))) =>
                unapply()
        )

  extension [T](signal: Signal[IterableOnce[T]])

    def :=>> (renderFunc: Computed[T] => DT.DTX)(using keyFunc: T => String): DT.DTX =
      DT.withUnapplyFunction[HTMLElement]: () =>
        val fragment = document.createElement("f").asInstanceOf[HTMLElement] // TODO custom component with "display: contents"
        val map = mutable.Map.empty[String, (State[T], DT.DTX, (Node, UnapplyFunction))]

        // add & update
        def update(iterable: IterableOnce[T]): Unit =
          val seq = iterable.iterator.toSeq
          val keyValueMap = seq.map(value => keyFunc(value) -> value)
          val orderedKeys = keyValueMap.map(_._1)

          keyValueMap.foreach: (key, value) =>
            map.updateWith(key):
              case None =>
                val newSignal = Signal(value)
                val renderable = renderFunc(newSignal.map(c => c))
                Some((newSignal, renderable, renderable.render()))
              case prev@Some((signal, _, _)) =>
                signal := value
                prev

          // remove
          map.keySet.diff(seq.map(keyFunc).toSet)
            .foreach: key =>
              val (_, renderable, (element, unapply)) = map.remove(key).get
              fragment.removeChild(element)
              unapply()

          // ordering
          orderedKeys.foreach: key =>
            val (_, _, (element, _)) = map(key)
            fragment.appendChild(element)


        val unsubscribe = signal.subscribe:
          update(_) // TODO can this be resolved with an effect?

        (
          fragment,
          () =>
            unsubscribe()
            map.foreach:
              case (_, (_, _, (_, unapply))) =>
                unapply()
        )

package net.apiduck.ducktape.render.native

import net.apiduck.ducktape.render.types.Renderable
import net.apiduck.ducktape.render.types.RenderableType
import net.apiduck.ducktape.signals.Signal
import net.apiduck.ducktape.signals.Signal.*
import net.apiduck.ducktape.signals.Signal.ListEntrySignal
import net.apiduck.ducktape.signals.Signal.State
import org.scalajs.dom.*

import scala.collection.mutable

trait SignalRenderer:

  extension [T](signal: State[Seq[T]])

    def :=>> (renderFunc: ListEntrySignal[T] => Renderable[RenderableType])(using keyFunc: T => String): Renderable[HTMLElement] = // TODO rewrite using Renderable.withUnapplyFunction
      Renderable.withUnapplyFunction[HTMLElement]: () =>
        val fragment = document.createElement("f").asInstanceOf[HTMLElement] // TODO custom component with "display: contents"
        val map = mutable.Map.empty[String, (State[T], Renderable[RenderableType], Node)]
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
                Some((newSignal, renderable, renderable.create()))
              case prev@Some((signal, _, _)) =>
                signal := value
                prev

          // remove
          map.keySet.diff(seq.map(keyFunc).toSet)
            .foreach: key =>
              val (_, renderable, element) = map.remove(key).get
              fragment.removeChild(element)
              renderable.destroy()

          // ordering
          keyIndexes.foreach: key =>
            val (_, _, element) = map(key)
            fragment.appendChild(element)


        val unsubscribe = signal.subscribe:
          update(_) // TODO can this be resolved with an effect?

        (
          fragment,
          () =>
            unsubscribe()
            map.foreach:
              case (_, (_, renderable, _)) =>
                renderable.destroy()
        )

  extension [T](signal: Signal[IterableOnce[T]])

    def :=>> (renderFunc: Computed[T] => Renderable[RenderableType])(using keyFunc: T => String): Renderable[HTMLElement] =
      Renderable.withUnapplyFunction[HTMLElement]: () =>
        val fragment = document.createElement("f").asInstanceOf[HTMLElement] // TODO custom component with "display: contents"
        val map = mutable.Map.empty[String, (State[T], Renderable[RenderableType], Node)]

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
                Some((newSignal, renderable, renderable.create()))
              case prev@Some((signal, _, _)) =>
                signal := value
                prev

          // remove
          map.keySet.diff(seq.map(keyFunc).toSet)
            .foreach: key =>
              val (_, renderable, element) = map.remove(key).get
              fragment.removeChild(element)
              renderable.destroy()

          // ordering
          orderedKeys.foreach: key =>
            val (_, _, element) = map(key)
            fragment.appendChild(element)


        val unsubscribe = signal.subscribe:
          update(_) // TODO can this be resolved with an effect?

        (
          fragment,
          () =>
            unsubscribe()
            map.foreach:
              case (_, (_, renderable, _)) =>
                renderable.destroy()
        )

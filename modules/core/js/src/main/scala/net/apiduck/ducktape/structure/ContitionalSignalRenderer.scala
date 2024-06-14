package net.apiduck.ducktape.structure

import net.apiduck.ducktape.DT
import net.apiduck.ducktape.web.signals.Signal
import org.scalajs.dom.Node
import net.apiduck.ducktape.util.unapply.*
import net.apiduck.ducktape.web.signals.Signal.ListEntrySignal
import net.apiduck.ducktape.web.signals.Signal.State
import scala.collection.mutable
import net.apiduck.ducktape.web.signals.Signal.WithState

// TODO extract the logic to spit Signal[Seq[T]] and Signal[IterableOnce[T]] into multiple signals in separate files
trait ConditionalSignalRenderer:

  type KeyType = String | Int | Double

  def If(condition: Signal[Boolean])(`then`: => DT.DTX): WithUnapplyFunction[Signal[Seq[Node]]] =
    If(condition)(`then`)(`else` = DT.Empty)

  def If(condition: Signal[Boolean])(`then`: => DT.DTX)(`else`: => DT.DTX = DT.Empty): WithUnapplyFunction[Signal[Seq[Node]]] =
    var previousUnapply: UnapplyFunction = ()
    condition.map(condition =>
      previousUnapply()
      val next = if condition then `then` else `else`
      val (nodes, unapply) = next.render()
      previousUnapply = unapply
      nodes
    ) -> (() => previousUnapply())

  // TODO Match may rerender every time
  def Match[E](value: Signal[E])(cases: PartialFunction[E, DT.DTX]): WithUnapplyFunction[Signal[Seq[Node]]] =
    var previousUnapply: UnapplyFunction = ()
    value.map(value => cases.applyOrElse(value, _ => DT.Empty)).map(next =>
      previousUnapply()
      val (nodes, unapply) = next.render()
      previousUnapply = unapply
      nodes
    ) -> (() => previousUnapply())

  def ForEach[E](in: WithState[Seq[E]], keyFn: E => KeyType)(render: ListEntrySignal[E] => DT.DTX): WithUnapplyFunction[Signal[Seq[Node]]] =
    val map = mutable.Map.empty[KeyType, (WithState[E], (Seq[Node], UnapplyFunction))]
    var keyIndexes: Seq[KeyType] = Nil

    val nodes = in.map: (seq: Seq[E]) =>

      def getCurrentIndex(key: KeyType): Int =
        keyIndexes.indexOf(key)

      val keyValueMap = seq.map(value => keyFn(value) -> value)
      keyIndexes = keyValueMap.map(_._1)

      // add & update
      keyValueMap.foreach: (key, value) =>
        map.updateWith(key):
          case None =>
            val newSignal = Signal.State(value)
            val renderable = render(ListEntrySignal(in, newSignal, () => getCurrentIndex(key)))
            Some(newSignal -> renderable.render())
          case prev@Some((signal, _)) =>
            signal := value
            prev

      // remove
      map.keySet.diff(seq.map(keyFn).toSet)
        .foreach: key =>
          val (_, (_, unapply)) = map.remove(key).get
          unapply()

      // to ordered seq
      keyIndexes.flatMap(key => map(key)._2._1)

    (
      nodes,
      () =>
        map.foreach:
          case (_, (_, (_, unapply))) =>
            unapply()
    )

  def ForEach[E](in: Signal[IterableOnce[E]], keyFn: E => KeyType)(render: Signal[E] => DT.DTX): WithUnapplyFunction[Signal[Seq[Node]]] =
    val map = mutable.Map.empty[KeyType, (WithState[E], (Seq[Node], UnapplyFunction))]
    var keyIndexes: Seq[KeyType] = Nil

    val nodes = in.map: (iterable: IterableOnce[E]) =>

      val keyValueMap = iterable.iterator.toSeq.map(value => keyFn(value) -> value)
      keyIndexes = keyValueMap.map(_._1)

      // add & update
      keyValueMap.foreach: (key, value) =>
        map.updateWith(key):
          case None =>
            val newSignal = State(value)
            val renderable = render(newSignal.map(v => v))
            Some(newSignal -> renderable.render())
          case prev@Some((signal, _)) =>
            signal := value
            prev

      // remove
      map.keySet.diff(iterable.iterator.toSet.map(keyFn))
        .foreach: key =>
          val (_, (elements, unapply)) = map.remove(key).get
          unapply()

      // to ordered seq
      keyIndexes.flatMap(key => map(key)._2._1)

    (
      nodes,
      () =>
        map.foreach:
          case (_, (_, (_, unapply))) =>
            unapply()
    )

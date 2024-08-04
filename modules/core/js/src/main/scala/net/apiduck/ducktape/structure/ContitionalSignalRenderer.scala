package net.apiduck.ducktape.structure

import net.apiduck.ducktape.DT
import net.apiduck.ducktape.DT.*
import net.apiduck.ducktape.web.signals.Signal
import org.scalajs.dom.Node
import net.apiduck.ducktape.util.unapply.*
import net.apiduck.ducktape.web.signals.Signal.ListEntrySignal
import net.apiduck.ducktape.web.signals.Signal.State
import scala.collection.mutable
import net.apiduck.ducktape.web.signals.Signal.WithState
import net.apiduck.ducktape.web.signals.Signal.MaybeSignal
import net.apiduck.ducktape.web.signals.Signal.SignalLike

// TODO extract the logic to spit Signal[Seq[T]] and Signal[IterableOnce[T]] into multiple signals in separate files
trait ConditionalSignalRenderer:

  type KeyType = String | Int | Double

  def SIf(condition: Signal[Boolean])(`then`: => DT.DTX): DT.DTX =
    SIf(condition)(`then`)()

  def SIf(condition: Signal[Boolean])(`then`: => DT.DTX)(`else`: => DT.DTX = DT.Empty): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[MaybeSignal[Seq[Node]]] =
        var previousUnapply: UnapplyFunction = ()
        WithUnapplyFunction(condition.map(condition =>
          previousUnapply()
          val next = if condition then `then` else `else`
          val WithUnapplyFunction(nodes, unapply) = next.render()
          previousUnapply = unapply
          SignalLike(nodes).get() // TODO how does this behave? is switching between signals handled correctly?
        ), () => previousUnapply())

  def SMatch[E](value: Signal[E])(cases: PartialFunction[E, DT.DTX]): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[MaybeSignal[Seq[Node]]] =
        var previousUnapply: UnapplyFunction = ()
        WithUnapplyFunction(value.map(value => cases.applyOrElse(value, _ => DT.Empty)).map(next =>
          previousUnapply()
          val WithUnapplyFunction(nodes, unapply) = next.render()
          previousUnapply = unapply
          SignalLike(nodes).get()
        ), () => previousUnapply())

  def SForEach[E](in: WithState[Seq[E]], keyFn: E => KeyType)(renderFn: ListEntrySignal[E] => DT.DTX): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[MaybeSignal[Seq[Node]]] =
        in.mapEntries(
          keyFn,
          create = (_, valueSignal) => renderFn(valueSignal).render(),
          postCreate = created => created._1,
          destroy = (_, created) => created._2(),
          reduce = _.flatMap(SignalLike(_).get())
        )

  def SForEach[E](in: WithState[Seq[E]], keyFn: (E, Int) => KeyType = (_:E,i)=>i)(renderFn: ListEntrySignal[E] => DT.DTX): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[MaybeSignal[Seq[Node]]] =
        in.mapEntriesWithIndex(
          keyFn,
          create = (_, valueSignal) => renderFn(valueSignal).render(),
          postCreate = created => created._1,
          destroy = (_, created) => created._2(),
          reduce = _.flatMap(SignalLike(_).get())
        )

  def SForEach[E](in: Signal[IterableOnce[E]], keyFn: E => KeyType)(renderFn: Signal[E] => DT.DTX): DT.DTX =
    new DT.DTX:
      override def render(): WithUnapplyFunction[MaybeSignal[Seq[Node]]] =
        val map = mutable.Map.empty[KeyType, (WithState[E], WithUnapplyFunction[MaybeSignal[Seq[Node]]])]
        var keyIndexes: Seq[KeyType] = Nil

        val nodes = in.map: (iterable: IterableOnce[E]) =>

          val keyValueMap = iterable.iterator.toSeq.map(value => keyFn(value) -> value)
          keyIndexes = keyValueMap.map(_._1)

          // add & update
          keyValueMap.foreach: (key, value) =>
            map.updateWith(key):
              case None =>
                val newSignal = State(value)
                val renderable = renderFn(newSignal.map(v => v))
                Some(newSignal -> renderable.render())
              case prev@Some((signal, _)) =>
                signal := value
                prev

          // remove
          map.keySet.diff(iterable.iterator.toSet.map(keyFn))
            .foreach: key =>
              val (_, WithUnapplyFunction(elements, unapply)) = map.remove(key).get
              unapply()

          // to ordered seq
          keyIndexes.flatMap(key => SignalLike(map(key)._2.value).get()) // TODO does this work?

        WithUnapplyFunction(
          nodes,
          () =>
            map.foreach:
              case (_, (_, WithUnapplyFunction(_, unapply))) =>
                unapply()
        )

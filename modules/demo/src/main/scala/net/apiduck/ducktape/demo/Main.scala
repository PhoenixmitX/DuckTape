package net.apiduck.ducktape.demo

import net.apiduck.ducktape.DT
import net.apiduck.ducktape.DuckTape
import net.apiduck.ducktape.compatibility.ElementType.DivElement
import net.apiduck.ducktape.structure.Structure
import net.apiduck.ducktape.structure.Structure.*
import net.apiduck.ducktape.web.signals.Signal
import net.apiduck.ducktape.web.signals.Signal.*
import org.scalajs.dom.*

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

object Main:

  @JSExportTopLevel("main")
  def main(args: Array[String]): Unit = {

    val personToGreet = Signal("world")

    val color = Signal(0) // HSL color

    val randomNumbers = Signal[Seq[Double]](Seq[Double]())
    val totalNumbers = randomNumbers.map(_.length)
    val sum = randomNumbers.map(_.sum)
    val average = sum.map(s => if totalNumbers.get() == 0 then 0 else s / totalNumbers.get())
    val min = randomNumbers.map(all => if all.length == 0 then 0 else all.min)
    val max = randomNumbers.map(all => if all.length == 0 then 0 else all.max)

    window.setTimeout(() => {
      personToGreet := "Scala.js"
    }, 2000)

    // does not trigger a 2nd re-render since the value is not effectively changed
    window.setTimeout(() => {
      personToGreet := "Scala.js"
    }, 3000)

    window.setInterval(() => {
      color := ((color.get() + 1) % 360)
    }, 1000 / 60)

    DuckTape.render(id = "app", renderable =
      div(
        h1(style := si"color: hsl($color, 75%, 50%); font-style: bold")(
          si"Hello, $personToGreet!",
        ),
        "Some static text!",
        br,
        button(onClick := { _ => randomNumbers modify { _ :+ js.Math.random() } })("Click Me!"),
        button(onClick := { _ => randomNumbers := Nil })("Reset"),
        button(onClick := { _ => randomNumbers modify { _.dropRight(1) } })("Undo"),
        button(onClick := { _ => randomNumbers modify { _.sorted } })("Sort"),
        button(onClick := { _ => randomNumbers modify { _.reverse } })("Reverse"),
        button(onClick := { _ => randomNumbers modify { Random.shuffle(_) } })("Shuffle"),
        br,
        si"Clicked: $totalNumbers times! Sum: $sum, Min: $min, Average: $average, Max: $max",
        ul(
          randomNumbers .keyFn (c => c) :=>> (Color)
        )
      )
    )
  }

  def Color(color: ListEntrySignal[Double]): DT.Tag[DivElement] =
    div(style := color.map(c => s"color: hsl(${c * 300}, 100%, 50%)"))(
      "Hello, world!",
      button(onClick := { _ => color := ((color.get() + 0.1) % 1) })("Change Color"),
      button(onClick := { _ => color.delete() })("Delete"),
      button(onClick := { _ => color.insertBefore(js.Math.random()) })("Insert Before"),
      button(onClick := { _ => color.insertAfter(js.Math.random()) })("Insert After"),
      button(onClick := { _ => color.insertTop(js.Math.random()) })("Insert At Top"),
      button(onClick := { _ => color.insertBottom(js.Math.random()) })("Insert At Bottom"),
    )

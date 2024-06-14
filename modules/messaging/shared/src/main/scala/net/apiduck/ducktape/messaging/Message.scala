package net.apiduck.ducktape.messaging

import io.bullet.borer.Decoder
import io.bullet.borer.Encoder
import io.bullet.borer.derivation.MapBasedCodecs.*

sealed trait Message[+Msg]
case class Tell[Msg](message: Msg) extends Message[Msg]
case class Ask[Msg](id: Long, question: Msg) extends Message[Msg]
case class Answer[Msg](id: Long, answer: Msg) extends Message[Msg]

object Message:
  def getEncoder[T](using encoder: Encoder[T]): Encoder[Message[T]] = deriveAllEncoders
  def getDecoder[T](using decoder: Decoder[T]): Decoder[Message[T]] = deriveAllDecoders

  def unapply[T](message: Message[T]): Option[T] = message match
    case Tell(message) => Some(message)
    case Ask(_, question) => Some(question)
    case Answer(_, answer) => Some(answer)

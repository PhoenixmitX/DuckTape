package net.apiduck.ducktape.messaging

import io.bullet.borer.Cbor
import io.bullet.borer.Decoder
import io.bullet.borer.Encoder

abstract class MessageRelay[SendMessage, ReceiveMessage](using
  networkLayer: NetworkLayer,
  sendEncoder: Encoder[SendMessage],
  receiveDecoder: Decoder[ReceiveMessage],
) {

  private var lastId = 0L

  private given messageEncoder: Encoder[Message[SendMessage]] = Message.getEncoder[SendMessage]
  private given messageDecoder: Decoder[Message[ReceiveMessage]] = Message.getDecoder[ReceiveMessage]

  def sendMessage(message: Message[SendMessage]): Unit =
    networkLayer.send(Cbor.encode(message))

  def onReceive(message: Message[ReceiveMessage]): Unit

  networkLayer.init: cbor =>
    onReceive(cbor.to[Message[ReceiveMessage]].value)

  // convienience methods to send a messages

  def tell(message: SendMessage): Unit =
    sendMessage(Tell(message))

  inline def ! (message: SendMessage): Unit = tell(message)

  def askId(message: SendMessage): Long =
    val id = lastId
    lastId += 1
    sendMessage(Ask(id, message))
    id

  def answerId(id: Long, message: SendMessage): Unit =
    sendMessage(Answer(id, message))

  def respondTo[Msg](message: Message[Msg], response: SendMessage): Unit = message match
    case Ask(id, _) => answerId(id, response)
    case _ => tell(response)
}

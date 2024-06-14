package net.apiduck.ducktape.messaging

import io.bullet.borer.Decoder
import io.bullet.borer.Encoder

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.concurrent.Promise

abstract class DefaultMessageRelay[Send, Receive](
  using NetworkLayer, Encoder[Send], Decoder[Receive],
) extends MessageRelay[Send, Receive]:
  import DefaultMessageRelay.*

  type TypedReceivedMessage = ReceivedMessage[Send, Receive]

  def onReceiveMessage(message: TypedReceivedMessage): Unit

  def onReceive(message: Message[Receive]): Unit =
    message match
      case Tell(message) => onReceiveMessage(ReceivedMessage(this, MessageType.Tell, message))
      case Ask(id, question) =>
        val askMsg = ReceivedMessage(this, MessageType.Ask(id), question)
        onReceiveMessage(askMsg)
        if !askMsg.isHandled then
          println(s"Received an ask message but it was not handled: $askMsg") // TODO logger.warn
          // TODO send failure response

      case Answer(id, answer) =>
        callbacks.remove(id)
          .map(_._2)
          .getOrElse:
            println(s"Received an answer for an unknown ask id: $id") // TODO logger.warn
            onReceiveMessage
          .apply(ReceivedMessage(this, MessageType.Answer(id), answer))

  private val callbacks = collection.mutable.Map.empty[Long, (Long, TypedReceivedMessage => Unit)]

  def askCallback(message: Send, callback: TypedReceivedMessage => Unit)(using timeout: FiniteDuration): Unit = // TODO implement timeout
    val id = askId(message)
    callbacks.put(id, (System.currentTimeMillis() + timeout.toMillis) -> callback)

  // TODO is there a type safe way to make the receive message type dependent on the send message type?
  def ask[R >: Receive](message: Send)(using timeout: FiniteDuration): Promise[R] =
    val promise = Promise[Receive]
    askCallback(message, msg => promise.success(msg.message))
    promise.asInstanceOf[Promise[R]]

object DefaultMessageRelay:
  enum MessageType:
    case Tell
    case Ask(id: Long)
    case Answer(id: Long)

  // TODO enforce reply like withEnforcedReplies in https://pekko.apache.org/api/pekko/snapshot/org/apache/pekko/persistence/typed/scaladsl/EventSourcedBehavior$.html
  case class ReceivedMessage[Send, Receive](relay: DefaultMessageRelay[Send, Receive], messageType: MessageType, message: Receive):

    /**
     * Whether the message needs a response.
     */
    def needsResponse: Boolean =
      messageType.isInstanceOf[MessageType.Ask]

    private var handled = false

    private def handle: Unit =
      if handled && needsResponse then
        throw new IllegalStateException("Cannot respond to an already handled ask message")
      handled = true

    /**
     * Whether the message has been handled.
     */
    def isHandled: Boolean =
      handled

    /**
      * Reply to the message with a response.
      *
      * @param response
      */
    def reply(response: Send): Unit =
      handle
      replyIgnoreHandled(response)

    private def replyIgnoreHandled(response: Send): Unit =
      messageType match
        case MessageType.Ask(id) =>
          relay.answerId(id, response)
          handled = true
        case _ => relay.tell(response)

    /**
      * Reply to the message with a future response.
      *
      * @param future
      */
    def reply(future: Future[Send])(using ExecutionContext): Unit =
      handle
      // TODO error handling
      future.foreach(replyIgnoreHandled)

    /**
     * Tells the relay that the message is handled and that the response will be sent later.
     * This is useful when the response is not immediately available.
     *
     * @return A function that can be used to send the response later.
     */
    def replyLater: Send => Unit = // TODO should we add an timeout?
      handle
      replyIgnoreHandled

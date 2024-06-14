package net.apiduck.ducktape.messaging.pekko

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.Behavior
import net.apiduck.ducktape.messaging.Message
import org.apache.pekko.actor.typed.ActorRef
import io.bullet.borer.Encoder
import io.bullet.borer.Decoder
import net.apiduck.ducktape.messaging.MessageRelay
import net.apiduck.ducktape.messaging.NetworkLayer

object DuckTapeMessagingActor:
  // TODO handle errors, add possibility to shutdown, ...
  // TODO add implementation based on DefaultMessageRelay
  // TODO add pekko based implementation for NetworkLayer
  def apply[Send, Receive](messageHandler: ActorRef[Message[Receive]])(using Encoder[Send], Decoder[Receive], NetworkLayer): Behavior[Message[Send]] =
    Behaviors.setup: context =>
      val relay = new MessageRelay[Send, Receive]:
        def onReceive(message: Message[Receive]): Unit =
          messageHandler ! message
      Behaviors.receiveMessage: message =>
        relay.sendMessage(message)
        Behaviors.same

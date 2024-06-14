package net.apiduck.ducktape.messaging

import io.bullet.borer.Encoder
import io.bullet.borer.Decoder

abstract class Api[C2S, S2C](using
  networkLayer: NetworkLayer,
  sendEncoder: Encoder[S2C],
  receiveDecoder: Decoder[C2S],
) extends DefaultMessageRelay[S2C, C2S]

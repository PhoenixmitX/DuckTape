package net.apiduck.ducktape.messaging

import io.bullet.borer.Encoder
import io.bullet.borer.Decoder

abstract class Api[C2S, S2C](using
  networkLayer: NetworkLayer,
  sendEncoder: Encoder[C2S],
  receiveDecoder: Decoder[S2C],
) extends DefaultMessageRelay[C2S, S2C]

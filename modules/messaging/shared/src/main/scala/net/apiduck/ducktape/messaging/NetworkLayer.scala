package net.apiduck.ducktape.messaging

import io.bullet.borer.DecodingSetup.{Api => DecodingApi}
import io.bullet.borer.EncodingSetup.{Api => EncodingApi}
import io.bullet.borer.Cbor.{DecodingConfig, EncodingConfig}

trait NetworkLayer:
  def send(message: EncodingApi[EncodingConfig]): Unit
  def init(receiveFn: DecodingApi[DecodingConfig] => Unit): Unit

package net.apiduck.ducktape.messaging

import org.scalajs.dom.{WebSocket, Blob}

import io.bullet.borer.DecodingSetup.{Api => DecodingApi}
import io.bullet.borer.EncodingSetup.{Api => EncodingApi}
import io.bullet.borer.Cbor.{DecodingConfig, EncodingConfig}
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import org.scalajs.dom.FileReader
import io.bullet.borer.Cbor
import scala.scalajs.js.typedarray.ArrayBuffer
import java.nio.ByteBuffer
import scala.scalajs.js.typedarray.TypedArrayBuffer

case class WebSocketNetworkLayer(url: String) extends NetworkLayer:
  private var ws: WebSocket = null

  def send(message: EncodingApi[EncodingConfig]): Unit =
    ws.send(message.toByteBuffer.arrayBuffer)

  def init(receiveFn: DecodingApi[DecodingConfig] => Unit): Unit =
    ws = new WebSocket(url)
    ws.onmessage = event =>
      val data = event.data.asInstanceOf[Blob]
      val reader = new FileReader()
      reader.onloadend = _ =>
        val arrayBuffer = reader.result.asInstanceOf[ArrayBuffer]
        val byteBuffer: ByteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
        receiveFn(Cbor.decode(byteBuffer))
      reader.readAsArrayBuffer(data)
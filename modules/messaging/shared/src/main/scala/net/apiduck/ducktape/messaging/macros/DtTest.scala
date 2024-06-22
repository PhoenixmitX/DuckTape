package net.apiduck.ducktape.messaging.macros

import scala.reflect.ClassTag
import scala.annotation.experimental

import io.bullet.borer.Encoder
import io.bullet.borer.Decoder

import io.bullet.borer.EncodingSetup.{Api => EncodingApi}
import io.bullet.borer.DecodingSetup.{Api => DecodingApi}
import io.bullet.borer.Cbor.EncodingConfig
import io.bullet.borer.Cbor.DecodingConfig
import io.bullet.borer.Cbor

trait MyTrait {
  def someMethod(arg1: Int, arg2: String): String
}

object Test:

  @experimental
  @main
  def main(): Unit =

    // val messageExecutor = MessageService.createExecutor(new MyTrait {
    //   def someMethod(arg1: Int, arg2: String): String = s"executed with $arg1 and $arg2"
    // })

    given MessageService with
      def executeMethod[T](targetClass: ClassTag[T], methodName: String, arguments: EncodingApi[EncodingConfig]): DecodingApi[DecodingConfig] =
        // Dummy implementation for demonstration
        println(s"Executing $methodName on $targetClass with arguments ${String(arguments.toByteArray)}")
        Cbor.decode(Cbor.encode("executed").toByteArray)
        // val ret = messageExecutor.executeMethod(methodName, Cbor.decode(arguments.toByteBuffer))
        // Cbor.decode(ret.toByteArray)

    // val myTraitInstance = MessageService.wrapService[MyTrait]
    val myTraitInstance = {
  class MyTraitImpl extends java.lang.Object with net.apiduck.ducktape.messaging.macros.MyTrait {
    override def someMethod(arg1: scala.Int, arg2: java.lang.String): scala.Predef.String = {
      val args: scala.collection.immutable.List[scala.Array[scala.Byte]] = scala.List.apply[scala.Any](arg1, arg2).zip[io.bullet.borer.Encoder[_ >: scala.Nothing <: scala.Any]](scala.List.apply[io.bullet.borer.Encoder[_ >: scala.Nothing <: scala.Any]](scala.compiletime.package$package.summonInline[io.bullet.borer.Encoder[scala.Int]], scala.compiletime.package$package.summonInline[io.bullet.borer.Encoder[java.lang.String]])).map[scala.Array[scala.Byte]](((x$1: scala.Tuple2[scala.Any, io.bullet.borer.Encoder[_ >: scala.Nothing <: scala.Any]]) => x$1 match {
        case scala.Tuple2(arg, encoder) =>
          io.bullet.borer.Cbor.encode[scala.Any](arg)(encoder.asInstanceOf[io.bullet.borer.Encoder[scala.Any]]).toByteArray
      }))
      scala.compiletime.package$package.summonInline[net.apiduck.ducktape.messaging.macros.MessageService].executeMethod[net.apiduck.ducktape.messaging.macros.MyTrait](scala.compiletime.package$package.summonInline[scala.reflect.ClassTag[net.apiduck.ducktape.messaging.macros.MyTrait]], "someMethod", io.bullet.borer.Cbor.encode[scala.collection.immutable.List[scala.Array[scala.Byte]]](args)(io.bullet.borer.Encoder.forLinearSeq[scala.Array[scala.Byte], scala.collection.immutable.List](io.bullet.borer.Encoder.forByteArrayDefault))).to[java.lang.String](scala.compiletime.package$package.summonInline[io.bullet.borer.Decoder[java.lang.String]]).value
    }
  }

  (new MyTraitImpl(): net.apiduck.ducktape.messaging.macros.MyTrait)
}


    println(myTraitInstance.someMethod(42, "Hello"))

package net.apiduck.ducktape.messaging.macros

import io.bullet.borer.Cbor
import io.bullet.borer.Cbor.DecodingConfig
import io.bullet.borer.Cbor.EncodingConfig
import io.bullet.borer.Decoder
import io.bullet.borer.DecodingSetup.Api as DecodingApi
import io.bullet.borer.Encoder
import io.bullet.borer.EncodingSetup.Api as EncodingApi

import scala.annotation.experimental
import scala.compiletime.constValue
import scala.compiletime.summonAll
import scala.compiletime.summonInline
import scala.quoted.*
import scala.reflect.ClassTag

trait MessageService {
  def executeMethod[T](targetClass: ClassTag[T], methodName: String, arguments: EncodingApi[EncodingConfig]): DecodingApi[DecodingConfig]
}

trait MessageExecutor[T] {
  def executeMethod(methodName: String, arguments: DecodingApi[DecodingConfig]): EncodingApi[EncodingConfig]
}

// the create instance macro does not work
// the newClass macro is an example of the macro documentation, it is hardcoded to work with the TestClass trait
// we need a macro that implements the logic of the createInstance macro
// but ist written in a way like the newClass macro
object MessageService:

  @experimental
  inline def wrapService[A]: A = ${ wrapServiceImpl[A] }

  @experimental
  def wrapServiceImpl[A: Type](using Quotes): Expr[A] =
    import quotes.reflect.*

    val typeSymbol = TypeRepr.of[A].typeSymbol

    // check if A is a trait
    if !typeSymbol.flags.is(Flags.Trait) then
      report.errorAndAbort(s"${typeSymbol.name} is not a trait")

    val name: String = typeSymbol.name + "Impl"
    val abstractMethods = typeSymbol.declaredMethods.filter(_.flags.is(Flags.Deferred))

    var newMethods = List.empty[DefDef]

    def decls(cls: Symbol): List[Symbol] =
      abstractMethods.map: method =>
        val methodDef = method.tree.asInstanceOf[DefDef]
        val methodName = method.name
        val returnType = methodDef.returnTpt.tpe
        val paramLists = method.paramSymss

        // Create parameters for the method implementation
        val params = paramLists.flatMap(_.map { param =>
          ValDef(param, None)
        })

        val mthd = Symbol.newMethod(
          cls,
          methodName,
          // MethodType(List("arg1", "arg2"))(m => params.map(_.tpt.tpe.appliedTo(m).widen.widenTermRefByName.widenByName.dealias.dealiasKeepOpaques.simplified.widen.widenTermRefByName.widenByName.dealias.dealiasKeepOpaques.simplified), _ => returnType),
          MethodType(method.paramSymss.flatMap(_.map(_.name)))(_ => /* method.paramSymss.flatten.map(_.info) */ List(TypeRepr.of[Int], TypeRepr.of[String]), _ => returnType),
          // MethodType(paramLists.flatMap(_.map(_.name)))(_ => paramLists.flatten.map(_.info), _ => returnType),
          // MethodType(paramLists.flatMap(_.map(_.name)))(_ => methodDef.paramss.flatMap(_.params.map{
          //   case ValDef(_ , param, _) => param.tpe
          //   case _: TypeDef => report.errorAndAbort("Type parameters are not supported")
          // }), _ => returnType),
          Flags.Override,
          Symbol.noSymbol
        )

        val arguments = paramLists.flatten.map(param => Ref(param).changeOwner(mthd))
        val allEncoders = arguments.map(_.tpe.widenTermRefByName.dealias.asType).map { case '[t] => '{summonInline[Encoder[t]]} }

        val methodBody = returnType.asType match {
          case '[ret] => '{
            val args = ${Expr.ofList(arguments.map(_.asExpr))/* (using Type.of[Any])(using method.asQuotes) */}.zip(${Expr.ofList(allEncoders)}).map { case (arg, encoder) => Cbor.encode(arg)(using encoder.asInstanceOf[Encoder[Any]]).toByteArray }
            summonInline[MessageService].executeMethod[A](
              summonInline[ClassTag[A]],
              ${Expr(methodName)},
              Cbor.encode(args)
            ).to[ret](using summonInline[Decoder[ret]]).value
          }.asTerm.changeOwner(mthd)
          case _ =>
            report.errorAndAbort(s"Unsupported return type for method $methodName")
        }

        newMethods = newMethods :+ DefDef(mthd, c => Some(methodBody)) // .changeOwner(cls)

        mthd

    val cls = Symbol.newClass(
      Symbol.spliceOwner,
      name,
      parents = List(TypeTree.of[AnyRef].tpe, TypeTree.of[A].tpe),
      decls,
      selfType = None
    )

    val clsDef = ClassDef(cls, parents = List(TypeTree.of[AnyRef], TypeTree.of[A]), body = newMethods)
    val newCls = Typed(Apply(Select(New(TypeIdent(cls)), cls.primaryConstructor), Nil), TypeTree.of[A])

    val block = Block(List(clsDef), newCls)
    report.info(block.show)
    block.asExprOf[A]


  // @experimental
  // inline def createExecutor[T](createFor: T): MessageExecutor[T] = ${ createExecutorImpl[T]('createFor) }

  // @experimental
  // def createExecutorImpl[T: Type](createFor: Expr[T])(using Quotes): Expr[MessageExecutor[T]] =

  //   // executes the method with the name of methodName, arguments are encoded with the EncodingApi
  //   // this is the opposite of the wrapService macro and the message service
  //   // we DO NOT WANT to use the MessageService here
  //   import quotes.reflect.*

  //   val typeSymbol = TypeRepr.of[T].typeSymbol

  //   val methods = typeSymbol.declaredMethods

  //   '{
  //     new MessageExecutor[T]:
  //       def executeMethod(methodName: String, arguments: DecodingApi[DecodingConfig]): EncodingApi[EncodingConfig] =

  //         ${
  //           val parsedArgs = '{arguments.to[List[Array[Byte]]].value}
  //           val ifs = methods.map { method =>
  //             val methodDef = method.tree.asInstanceOf[DefDef]
  //             val returnType = methodDef.returnTpt.tpe
  //             val paramLists = methodDef.paramss.flatMap(_.params.map:
  //               case valDef: ValDef => valDef.tpt.tpe
  //               case _ => report.errorAndAbort("Type parameters are not supported")
  //             )
  //             val allDecoders = paramLists.map(_.widenTermRefByName.dealias.asType).map { case '[t] => '{summonInline[Decoder[t]]} }

  //             returnType.asType match {
  //               case '[ret] => '{
  //                 val args = ${parsedArgs}.zip(allDecoders).map { case (arg, decoder) => Cbor.decode(arg).to(using ${decoder}.asInstanceOf[Decoder[Any]]).value }
  //                 if methodName == ${Expr(method.name)} then
  //                   // ${createFor}.asInstanceOf[T].${method.name}(args*)
  //                   methodDef.rhs.get.appliedTo(Term.betaReduce()).asExprOf[ret]


  //               }.asTerm
  //               case _ =>
  //                 report.errorAndAbort(s"Unsupported return type for method $methodName")
  //             }
  //           }
  //           '{ifs.reduceLeft((a, b) => ${'b ; 'a})}
  //           throw new RuntimeException(s"Method $methodName not found")
  //         }



  //   }.asExprOf[MessageExecutor[T]]


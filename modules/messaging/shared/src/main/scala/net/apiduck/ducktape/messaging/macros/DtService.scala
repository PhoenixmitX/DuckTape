package net.apiduck.ducktape.messaging.macros

import scala.quoted.*
import scala.reflect.ClassTag
import scala.compiletime.summonInline
import scala.annotation.experimental

trait MyExecutor {
  // TODO use Encoder and Decoder instead of ClassTag
  def executeMethod[T, R](targetClass: ClassTag[T], methodName: String, arguments: Seq[Any])(using ClassTag[R]): R
}


//     println(myTraitInstance.someMethod(42, "Hello"))

//     trait TestClass {
//       def func(s: String): String // = "base"
//     }

//     val res = NewClass.newClass[TestClass]

//     //{
//     //  class TestClassImpl extends TestClass {
//     //    override def func(s: java.lang.String): java.lang.String = "override"
//     //  }
//     //
//     //  (new TestClassImpl(): TestClass)
//     //}

//     println(res.func("xxx")) // override


@experimental
object MyExecutor {
  inline def createInstance[T](using executor: MyExecutor): T = ${ createInstanceImpl[T] }

  def createInstanceImpl[T: Type](using Quotes): Expr[T] = {
    import quotes.reflect.*

    val tpe = TypeRepr.of[T]

    // Ensure T is a trait
    if (!tpe.typeSymbol.flags.is(Flags.Trait)) {
      report.errorAndAbort(s"${tpe.typeSymbol.name} is not a trait")
    }

    // Generate implementations for all abstract methods
    val abstractMethods = tpe.typeSymbol.declaredMethods.filter(_.flags.is(Flags.Deferred))

    def decls(cls: Symbol): List[Symbol] =
      abstractMethods.map: method =>
        Symbol.newMethod(
          cls,
          method.name,
          method.typeRef,
          // Symbol.requiredMethod hodType(method.paramSymss, method.returnTpe),
          Flags.Override,
          Symbol.noSymbol
        )

    // Generate a class that implements the trait T
    val newClass = Symbol.newClass(
      Symbol.spliceOwner,
      "DuckTapeImpl" + tpe.typeSymbol.name, // "<anonymous>"
      parents = List(TypeTree.of[AnyRef].tpe, tpe),
      decls, // = _ => List(), // methodImpls.map(_.symbol).toList,
      selfType = None
    )

    val methodImpls = abstractMethods.map { method =>
      val methodName = method.name
      // val methodType = method.typeMembers
      val returnType = method.tree.asInstanceOf[DefDef].returnTpt.tpe.asType
      val paramLists = method.paramSymss

      // Create arguments sequence for executeMethod call
      val arguments = paramLists.flatten.map(param => Ref(param))

      // Implement the method by calling executeMethod
      val methodBody = returnType match {
        case '[ret] => '{
          val retCt: ClassTag[ret] = summonInline[ClassTag[ret]]
          val executor: MyExecutor = summonInline[MyExecutor]
          executor.executeMethod[T, ret](
            summonInline[ClassTag[T]],
            ${Expr(methodName)},
            Seq(${Varargs(arguments.map(arg => arg.asExpr))}*)
          )(using retCt)
        }.asTerm
        case _ =>
          report.errorAndAbort(s"Unsupported return type for method $methodName")
      }

      // report.info("newMethod =\n" + newMethod.show)
      DefDef(method, _ => Some(methodBody))
      // Method
    }


    // report.info("newClass =\n" + newClass.children.mkString)

    val cls = ClassDef(newClass, parents = List(TypeTree.of[AnyRef], TypeTree.of[T]), body = methodImpls)

    report.info(cls.show)

    val newClassInstance = Typed(Apply(Select(New(TypeIdent(newClass)), newClass.primaryConstructor), Nil), TypeTree.of[T])

    // report.info("newClassInstance =\n" + newClassInstance.show)

    Block(List(cls), newClassInstance).asExpr.asInstanceOf[Expr[T]]
  }
}

object NewClass {
  @experimental
  inline def newClass[A]: A = ${ newClassImpl[A] }

  @experimental
  def newClassImpl[A: Type](using Quotes): Expr[A] = {
    import quotes.reflect.*

    val name: String = TypeRepr.of[A].typeSymbol.name + "Impl"
    val parents = List(TypeTree.of[A])

    def decls(cls: Symbol): List[Symbol] =
      List(Symbol.newMethod(cls, "func", MethodType(List("s"))(_ => List(TypeRepr.of[String]), _ => TypeRepr.of[String]), Flags.Override, Symbol.noSymbol))

    val cls = Symbol.newClass(Symbol.spliceOwner, name, parents = TypeTree.of[AnyRef].tpe :: parents.map(_.tpe), decls, selfType = None)
    val funcSym = cls.declaredMethod("func").head

    val funcDef = DefDef(funcSym, argss => Some('{"override"}.asTerm))
    val clsDef = ClassDef(cls, parents, body = List(funcDef))
    val newCls = Typed(Apply(Select(New(TypeIdent(cls)), cls.primaryConstructor), Nil), TypeTree.of[A])

    Block(List(clsDef), newCls).asExprOf[A]
  }
}

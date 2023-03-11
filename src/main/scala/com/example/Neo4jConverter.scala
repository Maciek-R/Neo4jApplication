package com.example

import scala.reflect.ClassTag
import scala.reflect._
import scala.reflect.runtime.universe._

object Neo4jConverter {
  def fromMap[T: TypeTag : ClassTag](m: Map[String, _]) = {
    val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
    val classTest = typeOf[T].typeSymbol.asClass
    val classMirror = rm.reflectClass(classTest)
    val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror = classMirror.reflectConstructor(constructor)

    val constructorArgs = constructor.paramLists.flatten.map((param: Symbol) => {
      val paramName = param.name.toString
      if (param.typeSignature <:< typeOf[Option[Any]]) {
          util.Try(m.get(paramName)).toOption.flatten
      } else {
        m.get(paramName).getOrElse(throw new IllegalArgumentException("Map is missing required parameter named " + paramName))
      }
    })

    constructorMirror(constructorArgs: _*).asInstanceOf[T]
  }
}

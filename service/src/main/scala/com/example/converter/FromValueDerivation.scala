package com.example.converter

import magnolia1.{CaseClass, Magnolia}
import scala.language.experimental.macros

trait FromValueDerivation {

  type Typeclass[T] = FromValue[T]

  def join[T](caseClass: CaseClass[FromValue, T]): FromValue[T] =
    new FromValue[T] {
      override def fromValue(value: Option[Any]): T = {
        val map = value.map(_.asInstanceOf[Map[String, Any]]).get
        val yyy = caseClass.constructEither { param =>
          Right(param.typeclass.fromValue(map.get(param.label)))
        }
        yyy.right.get // TODO
      }
    }

  implicit def gen[T]: FromValue[T] = macro Magnolia.gen[T]
}

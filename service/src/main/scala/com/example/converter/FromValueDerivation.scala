package com.example.converter

import magnolia1.{CaseClass, Magnolia}
import scala.language.experimental.macros

trait FromValueDerivation {

  type Typeclass[T] = FromValue[T]

  def join[T](caseClass: CaseClass[FromValue, T]): FromValue[T] =
    new FromValue[T] {
      override def fromValue(
          value: Option[Any]
      ): Either[FromValueExtractionError, T] = {
        value.map(_.asInstanceOf[Map[String, Any]]) match {
          case Some(map) =>
            caseClass
              .constructEither { param =>
                param.typeclass.fromValue(map.get(param.label))
              }
              .left
              .map(errors => FromValueExtractionError(errors.mkString(", ")))
          case None => Left(FromValueExtractionError("Map error"))
        }
      }
    }

  implicit def gen[T]: FromValue[T] = macro Magnolia.gen[T]
}

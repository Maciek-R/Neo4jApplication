package com.example.converter

trait FromMap[T] {
  def fromMap(map: Map[String, Any]): Either[FromValueExtractionError, T]
}

object FromMap {

  def apply[T](implicit FM: FromMap[T]): FromMap[T] = FM

  implicit def generic[T: FromValue]: FromMap[T] =
    new FromMap[T] {
      override def fromMap(
          map: Map[String, Any]
      ): Either[FromValueExtractionError, T] =
        implicitly[FromValue[T]].fromValue(Some(map))
    }

}

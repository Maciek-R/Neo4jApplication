package com.example.converter

case class FromValueExtractionError(error: String)

trait FromValue[T] {
  def fromValue(
      value: Option[Any]
  ): Either[FromValueExtractionError, T]
}

trait MandatoryFromValue[T] extends FromValue[T] {
  def fromValue(value: Any): Either[FromValueExtractionError, T]

  def fromValue(value: Option[Any]): Either[FromValueExtractionError, T] = {
    value match {
      case Some(value) => fromValue(value)
      case None        => Left(FromValueExtractionError("Missing???"))
    }
  }
}

object FromValue extends FromValueDerivation {
  def apply[T](implicit fromValue: FromValue[T]): FromValue[T] = fromValue

  // TODO add more types

  implicit val str: FromValue[String] = new MandatoryFromValue[String] {
    override def fromValue(
        value: Any
    ): Either[FromValueExtractionError, String] = {
      value match {
        case v: String => Right(v)
        case _         => Left(FromValueExtractionError("errStr"))
      }
    }
  }

  implicit val int: FromValue[Int] = new MandatoryFromValue[Int] {
    override def fromValue(
        value: Any
    ): Either[FromValueExtractionError, Int] =
      value match {
        case v: Int => Right(v)
        case _      => Left(FromValueExtractionError("errInt"))
      }
  }

  implicit val bool: FromValue[Boolean] = new MandatoryFromValue[Boolean] {
    override def fromValue(
        value: Any
    ): Either[FromValueExtractionError, Boolean] =
      value match {
        case v: Boolean => Right(v)
        case _          => Left(FromValueExtractionError("errBool"))
      }
  }

  implicit def optionalConv[T](implicit FV: FromValue[T]): FromValue[Option[T]] = new Typeclass[Option[T]] {
    override def fromValue(value: Option[Any]): Either[FromValueExtractionError, Option[T]] = {
      value match {
        case Some(value) => FV.fromValue(Some(value)).map(Some(_))
        case None        => Right(None)
      }
    }
  }
}

package com.example.converter

import com.example.service.User.{UserId, UserIdTag}
import shapeless.tag.@@

trait FromValueExtractionError
case object MissingFieldError extends FromValueExtractionError
case class MissingFieldErrorDetails(error: String) extends FromValueExtractionError
case class ConversionError(error: String) extends FromValueExtractionError
case class FromValueExtractionErrors(errors: List[FromValueExtractionError]) extends FromValueExtractionError
case class MapError(error: String) extends FromValueExtractionError

object FromValueExtractionError {
  def apply(errors: List[FromValueExtractionError]) = FromValueExtractionErrors(errors)
}

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
      case None        => Left(MissingFieldError)
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
        case _         => Left(ConversionError(s"Cannot convert ${value} into String"))
      }
    }
  }

  implicit val int: FromValue[Int] = new MandatoryFromValue[Int] {
    override def fromValue(
        value: Any
    ): Either[FromValueExtractionError, Int] =
      value match {
        case v: Int => Right(v)
        case _      => Left(ConversionError(s"Cannot convert ${value} into Int"))
      }
  }

  implicit val bool: FromValue[Boolean] = new MandatoryFromValue[Boolean] {
    override def fromValue(
        value: Any
    ): Either[FromValueExtractionError, Boolean] =
      value match {
        case v: Boolean => Right(v)
        case _          => Left(ConversionError(s"Cannot convert ${value} into Boolean"))
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

  implicit def stringTagged[U]: FromValue[String @@ U] = tagged
  implicit def intTagged[U]: FromValue[Int @@ U] = tagged
  implicit def boolTagged[U]: FromValue[Boolean @@ U] = tagged

  private implicit def tagged[T: FromValue, U]: FromValue[T @@ U] = new MandatoryFromValue[T @@ U] {
    override def fromValue(value: Any): Either[FromValueExtractionError, T @@ U] = {
      value match {
        case v: (T @@ U) => implicitly[FromValue[T]].fromValue(Some(v)).map(shapeless.tag[U][T])
        case _           => Left(ConversionError(s"Cannot convert ${value} into tagged"))
      }
    }
  }
}

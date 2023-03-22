package com.example.converter

trait FromValue[T] {
  def fromValue(value: Option[Any]): T
}

object FromValue extends FromValueDerivation {
  def apply[T](implicit fromValue: FromValue[T]): FromValue[T] = fromValue

  // TODO add more types
  implicit val optStr: FromValue[Option[String]] =
    new Typeclass[Option[String]] {
      override def fromValue(value: Option[Any]): Option[String] = {
        value match {
          case Some(v: String) => Some(v)
          case _               => None
        }
      }
    }

  implicit val str: FromValue[String] = new Typeclass[String] {
    override def fromValue(value: Option[Any]): String = {
      value match {
        case Some(v: String) => v
        case _               => ""
      }
    }
  }

  implicit val int: FromValue[Int] = new Typeclass[Int] {
    override def fromValue(value: Option[Any]): Int =
      value match {
        case Some(v: Int) => v
        case _            => 0
      }
  }

  implicit val optInt: FromValue[Option[Int]] = new Typeclass[Option[Int]] {
    override def fromValue(value: Option[Any]): Option[Int] = {
      value match {
        case Some(v: Int) => Some(v)
        case _            => None
      }
    }
  }
}

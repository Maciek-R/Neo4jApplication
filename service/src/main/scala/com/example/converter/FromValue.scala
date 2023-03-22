package com.example.converter

trait FromValue[T] {
  def fromValue(value: Option[Any]): T
}

object FromValue extends FromValueDerivation {

  implicit val optStr: FromValue[Option[String]] =
    new Typeclass[Option[String]] {
      override def fromValue(value: Option[Any]): Option[String] = {
        value match {
          // TODO erasure
          case Some(x) if x.isInstanceOf[String] => Some(x.asInstanceOf[String])
          case _                                 => None
        }
      }
    }

  implicit val str: FromValue[String] = new Typeclass[String] {
    override def fromValue(value: Option[Any]): String = {
      value match {
        // TODO erasure
        case Some(x) if x.isInstanceOf[String] => x.asInstanceOf[String]
        case _                                 => ""
      }
    }
  }
//TODO add more types
  implicit val int: FromValue[Int] = new Typeclass[Int] {
    override def fromValue(value: Option[Any]): Int =
      value match {
        // TODO erasure
        case Some(x) if x.isInstanceOf[Int] => x.asInstanceOf[Int]
        case _                              => 0
      }
  }

  def apply[T](implicit fromValue: FromValue[T]): FromValue[T] = fromValue
}

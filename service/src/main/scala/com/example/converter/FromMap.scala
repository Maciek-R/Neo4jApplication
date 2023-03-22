package com.example.converter

trait FromMap[T] {
  def fromMap(map: Map[String, Any]): T
}

object FromMap {

  def apply[T](implicit FM: FromMap[T]): FromMap[T] = FM

  implicit def generic[T](implicit FV: FromValue[T]): FromMap[T] =
    new FromMap[T] {
      override def fromMap(map: Map[String, Any]): T = FV.fromValue(Some(map))
    }

}

case class Abc(str: String, str2: String, zxc: Int)

package com.example.service

import com.example.converter.FromMap

case class User(name: String, lastName: Option[String])

object User {
  import com.example.converter.FromMap._
  import com.example.converter.FromValue.gen

  implicit val userFromMap = FromMap.apply[User]
}

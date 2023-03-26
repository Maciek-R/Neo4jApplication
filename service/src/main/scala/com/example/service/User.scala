package com.example.service

import com.example.converter.{FromMap, FromValue}

case class User(id: String, name: String, lastName: Option[String], isAdmin: Boolean)

object User {
  import com.example.converter.FromMap._
  import com.example.converter.FromValue.gen

  implicit val userFromMap = FromMap.apply[User](FromMap.generic(FromValue.gen))
}

package com.example.service

import com.example.converter.{FromMap, FromValue}
import com.example.service.User.{IsAdmin, RandomNumber, UserId, UserLastName, UserName}
import shapeless.tag.@@

import scala.language.implicitConversions

case class User(
    id: UserId,
    name: UserName,
    lastName: Option[UserLastName],
    isAdmin: IsAdmin,
    randomNumber: RandomNumber
)

object User {
  import com.example.converter.FromMap._
  import com.example.converter.FromValue.gen

  implicit val userFromMap = FromMap.apply[User](FromMap.generic(FromValue.gen))

  trait UserIdTag
  type UserId = String @@ UserIdTag
  implicit def userId(userId: String): UserId = shapeless.tag[UserIdTag][String](userId)

  trait UserNameTag
  type UserName = String @@ UserNameTag
  implicit def userName(userName: String): UserName = shapeless.tag[UserNameTag][String](userName)

  trait UserLastNameTag
  type UserLastName = String @@ UserLastNameTag
  implicit def userLastName(userLastName: String): UserLastName = shapeless.tag[UserLastNameTag][String](userLastName)

  trait IsAdminTag
  type IsAdmin = Boolean @@ IsAdminTag
  implicit def userIsAdmin(isAdmin: Boolean): IsAdmin = shapeless.tag[IsAdminTag][Boolean](isAdmin)

  trait RandomNumberTag
  type RandomNumber = Int @@ RandomNumberTag
  implicit def userRandomNumber(number: Int): RandomNumber = shapeless.tag[RandomNumberTag][Int](number)
}

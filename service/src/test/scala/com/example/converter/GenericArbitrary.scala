package com.example.converter

import cats.data.NonEmptyList
import com.example.service.User
import org.scalacheck.{Arbitrary, Gen}

trait GenericArbitrary {

  implicit val strArbitrary: Arbitrary[String] = Arbitrary(Gen.uuid.map(_.toString))
  implicit val intArbitrary: Arbitrary[Int] = Arbitrary(Gen.numStr.map(_.toInt))
  implicit val boolArbitrary: Arbitrary[Boolean] = Arbitrary.arbBool

  implicit def optArbitrary[T: Arbitrary]: Arbitrary[Option[T]] =
    Arbitrary(Gen.option(implicitly[Arbitrary[T]].arbitrary))

  // TODO create ArbitraryDerivation magnolia
  implicit def nonEmptyListArbitrary[T: Arbitrary]: Arbitrary[NonEmptyList[T]] = {
    val genT = implicitly[Arbitrary[T]].arbitrary
    val nonEmptyListGen = for {
      list <- Gen.listOf(genT)
      elem <- genT
    } yield {
      NonEmptyList.fromListUnsafe(elem +: list)
    }
    Arbitrary(nonEmptyListGen)
  }

  implicit val userArbitrary = Arbitrary {
    for {
      id <- strArbitrary.arbitrary
      name <- strArbitrary.arbitrary
      lastName <- optArbitrary[String].arbitrary
      isAdmin <- boolArbitrary.arbitrary
    } yield User(id, name, lastName, isAdmin)
  }
}

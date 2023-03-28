package com.example.converter

import cats.data.NonEmptyList
import org.scalacheck.{Arbitrary, Gen}
import shapeless.tag.@@

trait GenericArbitrary extends ArbitraryDerivation {

  implicit val strArbitrary: Arbitrary[String] = Arbitrary(Gen.uuid.map(_.toString))
  implicit val intArbitrary: Arbitrary[Int] = Arbitrary(Gen.long.map(_.toInt))
  implicit val boolArbitrary: Arbitrary[Boolean] = Arbitrary.arbBool

  implicit def optArbitrary[T: Arbitrary]: Arbitrary[Option[T]] =
    Arbitrary(Gen.option(implicitly[Arbitrary[T]].arbitrary))

  implicit def nonEmptyListArbitrary[T: Arbitrary]: Arbitrary[NonEmptyList[T]] =
    Arbitrary(Gen.nonEmptyListOf(implicitly[Arbitrary[T]].arbitrary).map(NonEmptyList.fromListUnsafe))

  implicit def stringTagged[U]: Arbitrary[String @@ U] = taggedArbitrary
  implicit def intTagged[U]: Arbitrary[Int @@ U] = taggedArbitrary
  implicit def boolTagged[U]: Arbitrary[Boolean @@ U] = taggedArbitrary

  private def taggedArbitrary[T: Arbitrary, U]: Arbitrary[T @@ U] = Arbitrary {
    implicitly[Arbitrary[T]].arbitrary.map(shapeless.tag[U][T])
  }
}

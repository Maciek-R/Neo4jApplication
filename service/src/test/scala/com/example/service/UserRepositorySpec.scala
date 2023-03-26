package com.example.service

import cats.data.NonEmptyList
import com.example.Neo4jTestSupport
import com.example.converter.GenericArbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class UserRepositorySpec
    extends AnyFlatSpec
    with Neo4jTestSupport
    with Matchers
    with EitherValues
    with ScalaCheckPropertyChecks
    with GenericArbitrary {

  val userRepository = new UserRepositoryImpl(testAppConfig)

  implicit val userArbitrary: Arbitrary[User] = gen[User]

  implicit val config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 3)

  it should "return empty list" in forAll { _: User =>
    userRepository.readAll() shouldBe List.empty
  }

  it should "read all users" in forAll { (user1: User, user2: User) =>
    beforeEach()
    userRepository.create(user1) shouldBe true
    userRepository.create(user2) shouldBe true
    userRepository.readAll() should contain theSameElementsAs List(user1, user2)
  }

  it should "create users and read by ids" in forAll {
    (users: List[User]) => // TODO limit max size of users maybe in config?
      beforeEach()
      users.foreach(userRepository.create)
      println(userRepository.readAll().map(_.id))
      val toPick =
        Gen
          .pick(Gen.chooseNum(0, Math.max(users.size - 1, 0)).sample.get, users) // TODO
          .sample
          .get
          .toSeq // TODO create common method or trait
      userRepository.readByIds(toPick.map(_.id)) should contain theSameElementsAs toPick
  }

  it should "create users and get users by name" in forAll { (users: NonEmptyList[User]) =>
    users.toList.foreach(userRepository.create)
    val toPick = Gen.oneOf(users.toList).sample.get
    userRepository.getByName(toPick.name) should contain theSameElementsAs List(toPick)
  }
}

package com.example.service

import cats.data.NonEmptyList
import com.example.Neo4jTestSupport
import org.scalacheck.Arbitrary._
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
    with ScalaCheckPropertyChecks {

  val userRepository = new UserRepositoryImpl(testAppConfig)

  val strGen = Gen.uuid.map(_.toString)
  val strGen1 = Gen.uuid.map(_.toString)
  val optStrGen = Gen.option(strGen)
  val userGen = for {
    id <- Arbitrary(Gen.delay(strGen)).arbitrary
    name <- Arbitrary(strGen).arbitrary
    lastName <- Arbitrary(optStrGen).arbitrary
    isAdmin <- Arbitrary.arbBool.arbitrary
  } yield User(id, name, lastName, isAdmin)
//TODO move it to some common class
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

  implicit val userArbitrary = Arbitrary.apply(userGen)

  implicit val config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 3)

  it should "return empty list" in forAll { _: User =>
    userRepository.readAll() shouldBe List.empty
  }

  it should "read all users" in forAll { (user1: User, user2: User) =>
    userRepository.create(user1) shouldBe true
    userRepository.create(user2) shouldBe true
    userRepository.readAll() should contain theSameElementsAs List(user1, user2)
    afterEach()
  }

  it should "create users and read by ids" in forAll {
    (users: List[User]) => // TODO limit max size of users maybe in config?
      users.foreach(userRepository.create)
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

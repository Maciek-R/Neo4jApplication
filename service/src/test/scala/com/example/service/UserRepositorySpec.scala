package com.example.service

import com.example.Neo4jTestSupport
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UserRepositorySpec extends AnyFlatSpec with Neo4jTestSupport with Matchers with EitherValues {

  val userRepository = new UserRepositoryImpl(testAppConfig)

  it should "create user and read all users" in {
    val user = User("name", Some("lastName"), true)

    userRepository.readAll() should contain theSameElementsAs List.empty
    userRepository.create(user) shouldBe true
    userRepository.readAll() should contain theSameElementsAs List(user)
  }

  it should "create users and read all users" in {
    val user1 = User("name1", Some("lastName"), true)
    val user2 = User("name2", None, false)
    val user3 = User("name3", Some("lastName2"), true)

    userRepository.readAll() should contain theSameElementsAs List.empty
    userRepository.create(user1) shouldBe true
    userRepository.create(user2) shouldBe true
    userRepository.create(user3) shouldBe true
    userRepository.readAll() should contain theSameElementsAs List(user1, user2, user3)
  }

  it should "create users and get users by name" in {
    val user1 = User("name1", Some("lastName"), true)
    val user2 = User("name1", None, false)
    val user3 = User("name3", Some("lastName2"), true)

    userRepository.readAll() should contain theSameElementsAs List.empty
    userRepository.create(user1) shouldBe true
    userRepository.create(user2) shouldBe true
    userRepository.create(user3) shouldBe true
    userRepository.getByName("name1") should contain theSameElementsAs List(user1, user2)
  }
}

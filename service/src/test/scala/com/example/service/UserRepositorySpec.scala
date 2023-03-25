package com.example.service

import com.example.service.CypherInterpolator.CypherOps
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, EitherValues}

class UserRepositorySpec
    extends AnyFlatSpec
    with Matchers
    with EitherValues
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  val appConfig = AppConfig(
    DataBaseConfig("bolt://localhost/7687", Neo4jCredentials("neo4j", "neo4jneo4j"))
  ) // TODO update to different instance than main database
  val userRepository = new UserRepositoryImpl(appConfig)

  override def beforeEach(): Unit = {
    deleteAll()
  }

  override def afterAll(): Unit = {
    deleteAll()
  }

  private def deleteAll(): Unit = {
    val query = cypher"MATCH (all) DELETE all"

    Query(query, appConfig).execute { result =>
      result.consume().counters().nodesDeleted()
    }
  }

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

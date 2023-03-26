package com.example.service

import com.example.service.CypherInterpolator.CypherOps

trait UserRepository {

  def create(user: User): Boolean
  def getByName(name: String): List[User]
  def readByIds(ids: Seq[String]): List[User]
  def readAll(): List[User]
}

class UserRepositoryImpl(appConfig: AppConfig) extends UserRepository {
  override def create(user: User): Boolean = {
    val query =
      cypher"CREATE (user:Users{id: ${user.id}, name: ${user.name}, lastName: ${user.lastName}, isAdmin: ${user.isAdmin}})"

    val result = Query(query, appConfig).execute { result =>
      result.consume().counters().nodesCreated() == 1
    }

    result
  }

  override def getByName(name: String): List[User] = {
    val query =
      cypher"MATCH (user:Users{name: ${name}}) RETURN user.id as id, user.name as name, user.lastName as lastName, user.isAdmin as isAdmin"

    val results = Query(query, appConfig).execute { result =>
      QueryResult.list[User](result)
    }

    results
  }
  override def readAll(): List[User] = {
    val query =
      s"MATCH (user:Users) RETURN user.id as id, user.name as name, user.lastName as lastName, user.isAdmin as isAdmin"

    val results = Query(query, appConfig).execute { result =>
      QueryResult.list[User](result)
    }

    results
  }

  override def readByIds(ids: Seq[String]): List[User] = {
    val query =
      s"MATCH (user:Users)" + s" WHERE user.id IN [${ids.map(s => s"'${s}'").mkString(",")}]" +
        s" RETURN user.id as id, user.name as name, user.lastName as lastName, user.isAdmin as isAdmin"

    val results = Query(query, appConfig).execute { result =>
      QueryResult.list[User](result)
    }

    results
  }
}

package com.example

import com.example.User.User

trait UserRepository {

  def create(user: User): Boolean
  def getByName(name: String): List[User]
  def readAll(): List[User]
}

class UserRepositoryImpl(appConfig: AppConfig) extends UserRepository {
  override def create(user: User): Boolean = {
    val script = s"CREATE (user:Users{name: '${user.name}', lastName: '${user.lastName.orNull}'})"

    val result = Query(script, appConfig).execute { result =>
      result.consume().counters().nodesCreated() == 1
    }

    result
  }

  override def getByName(name: String): List[User] = {
    val script = s"MATCH (user:Users{name: '${name}'}) RETURN user.name as name, user.lastName as lastName"

    val results = Query(script, appConfig).execute { result =>
      QueryResult.list[User](result)
    }

    results
  }
  override def readAll(): List[User] = {
    val script = s"MATCH (user:Users) RETURN user.name as name, user.lastName as lastName"

    val results = Query(script, appConfig).execute{result =>
      QueryResult.list[User](result)
    }

    results
  }
}

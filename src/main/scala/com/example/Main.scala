package com.example

import org.neo4j.driver.GraphDatabase

object Main {
  def main(args: Array[String]) = {
    val appConfig = AppConfig(DataBaseConfig("bolt://localhost/7687", Neo4jCredentials("neo4j", "neo4jneo4j")))
    val userRepository: UserRepository = new UserRepositoryImpl(appConfig)

    val created = userRepository.create(User.User("mac", None))
    val usersByName = userRepository.getByName("mac")

    val users = userRepository.readAll()
    users.foreach(println)
    println("---------------------")
    usersByName.foreach(println)
  }
}

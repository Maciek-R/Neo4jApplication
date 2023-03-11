package com.example

import org.neo4j.driver.GraphDatabase


object Main {
  def main(args: Array[String]) = {
    val appConfig = AppConfig(DataBaseConfig("bolt://localhost/7687", Neo4jCredentials("neo4j", "neo4jneo4j")))
    val userRepository = new UserRepository(appConfig)

    val users = userRepository.readAll()
    println(users)
  }
}

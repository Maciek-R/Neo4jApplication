package com.example

import org.neo4j.driver.{AuthTokens, GraphDatabase}

object Main {
  def main(args: Array[String]) = {
    val driver = GraphDatabase.driver("bolt://localhost/7687", AuthTokens.basic("neo4j", "neo4jneo4j"))
    val session = driver.session()

    val name = "exampleName"
    val script = s"CREATE (user:Users {name:'${name}'})"

    val result = session.run(script)
    session.close()
    driver.close()

    val res = result.consume()
    println(res)
  }
}

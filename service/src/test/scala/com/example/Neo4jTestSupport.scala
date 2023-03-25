package com.example

import com.example.service.CypherInterpolator.CypherOps
import com.example.service.{AppConfig, DataBaseConfig, Neo4jCredentials, Query}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}

trait Neo4jTestSupport extends BeforeAndAfterEach with BeforeAndAfterAll {
  this: Suite =>

  val testAppConfig = AppConfig(DataBaseConfig("bolt://localhost/7687", Neo4jCredentials("neo4j", "neo4jneo4j")))

  override def beforeEach(): Unit = {
    deleteAll()
  }

  override def afterAll(): Unit = {
    deleteAll()
  }

  private def deleteAll(): Unit = {
    val query = cypher"MATCH (all) DELETE all"

    Query(query, testAppConfig).execute { result =>
      result.consume().counters().nodesDeleted()
    }
  }
}

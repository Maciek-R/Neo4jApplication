package com.example.service

import com.example.converter.FromMap
import org.neo4j.driver.types.TypeSystem
import org.neo4j.driver.{AuthTokens, GraphDatabase, Result}

import scala.jdk.CollectionConverters._

object QueryResult {
  def list[T: FromMap](result: Result): List[T] = {
    result.asScala.toList.map { r =>
      val fields = {
        r.fields().asScala.toList.map(p => (p.key(), p.value())).flatMap {
          case (key, value) =>
            if (value.hasType(TypeSystem.getDefault.STRING())) {
              Some((key, value.asString()))
            } else if (value.hasType(TypeSystem.getDefault.INTEGER()))
              Some((key, value.asInt()))
            // todo add other types
            else {
              None
            }
        }
      }
      implicitly[FromMap[T]].fromMap(fields.toMap)
    }
  }
}

case class Query(query: String, appConfig: AppConfig) {
  def execute[T](fconv: Result => T) = {
    val neo4jCredentials = appConfig.dataBaseConfig.neo4jCredentials
    val driver = GraphDatabase.driver(
      appConfig.dataBaseConfig.uri,
      AuthTokens.basic(neo4jCredentials.username, neo4jCredentials.password)
    )
    val session = driver.session()

    val result = session.run(query)
    val convertedResult = fconv(result)

    session.close()
    driver.close()

    convertedResult
  }
}

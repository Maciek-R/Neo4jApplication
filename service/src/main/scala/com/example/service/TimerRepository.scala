package com.example.service

import com.example.service.CypherInterpolator.CypherOps

trait TimerRepository {
  def getTimer(): Timer
  def updateCurrentTime(currentTime: Int): Timer
}

case class Timer(id: String, timeInSeconds: Int)

class TimerRepositoryImpl(appConfig: AppConfig) extends TimerRepository {
  override def getTimer(): Timer = {
    val query =
      cypher"MATCH (timer:Timer{id: 'current'}) RETURN timer.id as id, timer.timeInSeconds as timeInSeconds"

    val result = Query(query, appConfig).execute { result =>
      QueryResult.list[Timer](result)
    }.headOption

    result.getOrElse {
      val query =
        cypher"CREATE (timer: Timer{id: 'current', timeInSeconds:0}) RETURN timer.id as id, timer.timeInSeconds as timeInSeconds"
      Query(query, appConfig).execute { result =>
        QueryResult.list[Timer](result)
      }.head // TODO
    }
  }

  override def updateCurrentTime(currentTime: Int): Timer = {
    val query =
      cypher"MERGE (timer: Timer{id: 'current'}) ON MATCH SET timer.timeInSeconds = $currentTime RETURN timer.id as id, timer.timeInSeconds as timeInSeconds"

    val result = Query(query, appConfig)
      .execute { result =>
        QueryResult.list[Timer](result)
      }
      .headOption
      .get // TODO

    result
  }
}

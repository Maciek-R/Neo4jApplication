package com.example.service

import cats.effect._
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object Main extends IOApp {

  val appConfig = AppConfig(
    DataBaseConfig(
      "bolt://localhost/7687",
      Neo4jCredentials("neo4j", "neo4jneo4j")
    )
  )

  val userRepository: UserRepository = new UserRepositoryImpl(appConfig)
  val users = userRepository.readAll()
  users.foreach(println)

  val userRoutes: UserRoutes = new UserRoutes(userRepository)

  val service = userRoutes.routes
  val httpApp = Router("/" -> service).orNotFound
  override def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}

package com.example

import cats.effect.IO
import org.http4s.HttpRoutes
import org.neo4j.driver.GraphDatabase
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._
import org.http4s.server.Router

object Main extends IOApp {

  val appConfig = AppConfig(
    DataBaseConfig(
      "bolt://localhost/7687",
      Neo4jCredentials("neo4j", "neo4jneo4j")
    )
  )
  val userRepository: UserRepository = new UserRepositoryImpl(appConfig)

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name => Ok(s"Hello $name")
    case GET -> Root / "users" =>
      val users = userRepository.readAll()
      Ok(users.mkString("\n")) // TODO add encoders
    case GET -> Root / "users" / "name" / name =>
      val users = userRepository.getByName(name)
      Ok(users.mkString("\n"))
    case GET -> Root / "users" / "create" / name / lastName => // TODO change it to POST
      val created = userRepository.create(User.User(name, Some(lastName)))
      Ok(created.toString)
  }
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

package com.example.service

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import cats.syntax.all._
import com.comcast.ip4s._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import scala.concurrent.duration._

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

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello $name")
    case GET -> Root / "users" =>
      val users = userRepository.readAll()
      Ok(users.mkString("\n")) // TODO add encoders
    case GET -> Root / "users" / "name" / name =>
      val users = userRepository.getByName(name)
      Ok(users.mkString("\n"))
    case GET -> Root / "users" / "create" / name / lastName / isAdmin => // TODO change it to POST
      val created = userRepository.create(User(name, Some(lastName), isAdmin.toLowerCase == "true"))
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

package com.example.service

import cats.effect._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s._
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.io._

import scala.concurrent.duration.DurationInt
import scala.util.Random

class UserRoutes(userRepository: UserRepository) {

  implicit val userEntityEncoder: EntityEncoder[IO, List[Json]] = jsonEncoderOf
  implicit val boolEncoder: EntityEncoder[IO, Boolean] = jsonEncoderOf

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "hello" / name =>
        Ok(s"Hello $name")
      case GET -> Root / "users" =>
        val users = userRepository.readAll()
        Ok(users.map(_.asJson))
//      case GET -> Root / "users" / "add" =>
//        val io = Temporal[IO].sleep(5.seconds) >> IO.delay {
//          userRepository.create(User((new Random).nextString(10), "scheduler", Some("lastName"), true))
//        }
//        Ok(io)
      case GET -> Root / "users" / "name" / name =>
        val users = userRepository.getByName(name)
        Ok(users.map(_.asJson))
      case GET -> Root / "users" / "create" / name / lastName / isAdmin => // TODO change it to POST
        val created =
          userRepository.create(User((new Random).nextString(10), name, Some(lastName), isAdmin.toLowerCase == "true"))
        Ok(created.toString)
    }
  }

}

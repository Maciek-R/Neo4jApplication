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

class TimerRoutes(timerRepository: TimerRepository) {

  implicit val userEntityEncoder: EntityEncoder[IO, List[Json]] = jsonEncoderOf
  implicit val boolEncoder: EntityEncoder[IO, Boolean] = jsonEncoderOf

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] { case GET -> Root / "users" / "add" =>
//        val io = Temporal[IO].sleep(5.seconds) >> IO.delay {
//          userRepository.create(User((new Random).nextString(10), "scheduler", Some("lastName"), true))
//        }
//        Ok(io)
      val timer = timerRepository.getTimer()
      println(timer)
      Ok("Timer")
    }
  }

}

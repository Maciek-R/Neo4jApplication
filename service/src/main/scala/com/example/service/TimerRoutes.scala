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
    HttpRoutes.of[IO] { case GET -> Root / "timer" =>
      val timer = timerRepository.getTimer()
      Ok(s"Timer: ${timer}")
    }
  }

}

package com.example.service

import cats.effect._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s._
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.io._
import shapeless.tag.@@

import scala.concurrent.duration.DurationInt
import scala.util.Random

class UserRoutes(userRepository: UserRepository) {

  implicit val userEntityEncoder: EntityEncoder[IO, List[Json]] = jsonEncoderOf
  implicit val boolEncoder: EntityEncoder[IO, Boolean] = jsonEncoderOf

  implicit def stringTaggedEncoder[U]: EntityEncoder[IO, String @@ U] = EntityEncoder.stringEncoder.contramap {
    case x => x
  }

  implicit def intTaggedEncoder[U]: EntityEncoder[IO, Int @@ U] = EntityEncoder.stringEncoder.contramap { case x =>
    x.toString
  }

  implicit def boolTaggedEncoder[U]: EntityEncoder[IO, Boolean @@ U] = EntityEncoder.stringEncoder.contramap { case x =>
    x.toString
  }

//  implicit def strEncoder: Encoder[String] = Encoder.encodeString
  // TODO encoders
  implicit def userEncoder: Encoder[User] = Encoder.encodeJson.contramap { case x =>
    Json.obj("id" -> Encoder.encodeString.apply(x.id), "name" -> Encoder.encodeString.apply(x.name))
  }

  private val random = new Random

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
      case GET -> Root / "hello" / name =>
        Ok(s"Hello $name")
      case GET -> Root / "users" =>
        val users = userRepository.readAll()
        Ok(users.map(_.asJson))
      case GET -> Root / "users" / "name" / name =>
        val users = userRepository.getByName(name)
        Ok(users.map(_.asJson))
      case GET -> Root / "users" / "create" / name / lastName / isAdmin => // TODO change it to POST
        val created =
          userRepository.create(
            User(random.nextString(10), name, Some(lastName), isAdmin.toLowerCase == "true", random.nextInt())
          )
        Ok(created.toString)
    }
  }

}

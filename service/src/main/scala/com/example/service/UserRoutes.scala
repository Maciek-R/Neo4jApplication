package com.example.service

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

class UserRoutes(userRepository: UserRepository) {

  def routes: HttpRoutes[IO] = {
    HttpRoutes.of[IO] {
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
  }

}

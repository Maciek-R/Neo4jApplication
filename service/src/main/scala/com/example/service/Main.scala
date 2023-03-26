package com.example.service

import cats.effect._
import cats.effect.unsafe.implicits.global
import com.comcast.ip4s._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import cats.syntax.semigroupk._

import scala.concurrent.duration.DurationInt

object Main extends IOApp {

  val appConfig = AppConfig(
    DataBaseConfig(
      "bolt://localhost/7687",
      Neo4jCredentials("neo4j", "neo4jneo4j")
    )
  )

  val userRepository: UserRepository = new UserRepositoryImpl(appConfig)
  val timerRepository: TimerRepository = new TimerRepositoryImpl(appConfig)

  val userRoutes: UserRoutes = new UserRoutes(userRepository)
  val timerRoutes: TimerRoutes = new TimerRoutes(timerRepository)

  val service = userRoutes.routes <+> timerRoutes.routes
  val httpApp = Router("/" -> service).orNotFound

  override def run(args: List[String]): IO[ExitCode] = { // TODO fix logging
    periodicTimeUpdate().unsafeRunAsync {
      case Right(_)    => println("END")
      case Left(value) => println(s"Throwable: $value")
    }

    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

  private def periodicTimeUpdate(): IO[Unit] = {
    val result = for {
      timer <- IO.delay(timerRepository.getTimer())
      _ <- IO.println(s"Current system time online in seconds: ${timer.timeInSeconds}")
      _ <- Temporal[IO].sleep(10.seconds)
      _ <- IO.delay(timerRepository.updateCurrentTime(timer.timeInSeconds + 10))
    } yield (())

    result >> periodicTimeUpdate()
  }
}

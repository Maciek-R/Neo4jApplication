ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val http4sVersion = "1.0.0-M39"
val circeVersion = "0.14.5"

lazy val service = (project in file("service"))
  .settings(
    name := "service",
    libraryDependencies ++= Seq(
      "org.neo4j" % "neo4j" % "5.5.0",
      "org.neo4j.driver" % "neo4j-java-driver" % "5.6.0",
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.softwaremill.magnolia1_2" %% "magnolia" % "1.1.3",
      "org.scalatest" %% "scalatest" % "3.2.15" % "test"
    )
  )

lazy val root = (project in file("."))
  .aggregate(service)
  .settings(
    name := "root"
  )

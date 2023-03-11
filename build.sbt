ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Neo4JApplication",
    libraryDependencies ++= Seq(
      "org.neo4j" % "neo4j" % "5.5.0",
      "org.neo4j.driver" % "neo4j-java-driver" % "5.6.0"
    )
  )

package com.example

case class AppConfig(dataBaseConfig: DataBaseConfig)

case class DataBaseConfig(uri: String, neo4jCredentials: Neo4jCredentials)

case class Neo4jCredentials(username: String, password: String)

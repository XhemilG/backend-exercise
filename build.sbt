name := """backend-exercise"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

val akkaManagementVersion = "1.0.0"
val akkaVersion = "2.6.8"
val akkaHTTPVersion = "10.1.10"

scalaVersion := "2.12.9"

libraryDependencies ++= Seq(
  javaWs,
  guice,
  ehcache,
  filters,
  "junit" % "junit" % "4.12",
  "org.mongodb" % "mongodb-driver-sync" % "4.1.0",
  "org.projectlombok" % "lombok" % "1.18.12",
  "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "2.0.0",

  "org.hibernate" % "hibernate-validator" % "6.1.5.Final",
  "org.glassfish" % "javax.el" % "3.0.0",
  // akka related stuff
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,

  "com.github.karelcemus" %% "play-redis" % "2.5.0",
  "io.igl" %% "jwt" % "1.2.2"
)

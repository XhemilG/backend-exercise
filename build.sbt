name := """backend-exercise"""
organization := "com.example"

version := "1.0.0"

routesGenerator := InjectedRoutesGenerator

PlayKeys.devSettings += "config.resource" -> "development.conf"

fork in Test := true
javaOptions in Test ++= Seq("-Dconfig.file=conf/test.conf", "-Dlogger.resource=test.logback.xml")

val akkaManagementVersion = "1.0.0"
val akkaVersion = "2.6.8"
val akkaHTTPVersion = "10.1.12"

sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

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
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
  // akka cluster related stuff
  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion,
  // akka htttp related stuff
  "com.typesafe.akka" %% "akka-http-core" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http2-support" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHTTPVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHTTPVersion,

  "com.github.karelcemus" %% "play-redis" % "2.5.0",
  "io.igl" %% "jwt" % "1.2.2"
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)
resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.sbtPluginRepo("releases")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val root = project.in(file(".")).enablePlugins(PlayScala)

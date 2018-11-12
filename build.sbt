
name := "kinesis-cqrs-test"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

PlayKeys.devSettings := Seq("play.server.http.port" -> "9000")

scalaVersion := "2.11.8"


PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)
PB.protoSources in Compile := Seq(
  file("app/protobuf")
)
libraryDependencies ++= Seq(
  ehcache,
  guice,
  ws,
  "org.xerial" % "sqlite-jdbc" % "3.16.1",
  "com.brsanthu" % "migbase64" % "2.2",
  "com.markatta" %% "akron" % "1.2",
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.18",
  "com.typesafe.akka" %% "akka-persistence-dynamodb" % "1.1.1",
  "com.typesafe.akka" %% "akka-remote" % "2.5.18",
  "com.streetcontxt" %% "kcl-akka-stream" % "2.0.3" excludeAll (
    ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-core"),
    ExclusionRule(organization = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-cbor")
    ),
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.7",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.9.7",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"

)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.17",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0"
).map(_ % Test)


resolvers in ThisBuild += Resolver.bintrayRepo("streetcontxt", "maven")
name := """reactive-scala-labs"""

version := "1.2"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.4",
  "com.typesafe.akka" %% "akka-remote" % "2.5.4",
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.10",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.18.0",
  "org.json4s" %% "json4s-native" % "3.5.3",
  "org.json4s" %% "json4s-core" % "3.5.3",
  "org.iq80.leveldb" % "leveldb" % "0.9",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % "test"
)

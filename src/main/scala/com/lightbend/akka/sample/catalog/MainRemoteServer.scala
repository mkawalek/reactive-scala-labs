package com.lightbend.akka.sample.catalog

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object MainRemoteServer extends App {

  val config = ConfigFactory.load("app.conf")

  val serversystem = ActorSystem("Reactive5", config.getConfig("serverapp").withFallback(config))
  serversystem.actorOf(Props[ProductCatalog], "catalog")

}

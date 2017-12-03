package com.lightbend.akka.sample.catalog

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.lightbend.akka.sample.catalog.CatalogCommands.GetBySearch
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object MainRemoteClient extends App {

  val config = ConfigFactory.load("app.conf")

  val clientsystem = ActorSystem("Reactive5", config.getConfig("clientapp").withFallback(config))

  val client = clientsystem.actorOf(Props(new Actor {
    implicit val ec = context.dispatcher
    implicit val timeout = Timeout(2 seconds)

    override def receive = {
      case Init =>
        val catalog = context.actorSelection("akka.tcp://Reactive5@127.0.0.1:2552/user/catalog")

        (catalog ? GetBySearch("Fanta Peep")).pipeTo(sender())

    }
  }), "client")

  implicit val timeout = Timeout(5 seconds)

  (client ? Init).foreach(println)(clientsystem.dispatcher)

  case object Init

}

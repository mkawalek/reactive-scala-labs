package com.lightbend.akka.sample.basic.normal.cart

import akka.actor.{ActorSystem, Props}
import com.lightbend.akka.sample.basic.Commons._
import com.typesafe.config.ConfigFactory

object Main extends App {

  val config = ConfigFactory.load("app.conf")

  private implicit val system = ActorSystem("test", config)

  private val actor = system.actorOf(Props(new CartManager("1")))

  testCart(actor)

}

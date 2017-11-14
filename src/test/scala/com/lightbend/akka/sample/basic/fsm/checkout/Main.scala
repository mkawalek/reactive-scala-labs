package com.lightbend.akka.sample.basic.fsm.checkout

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.lightbend.akka.sample.basic.Commons._
import com.lightbend.akka.sample.basic.normal.checkout.CheckoutActor
import com.lightbend.akka.sample.commonDefs.IdProvider
import com.typesafe.config.ConfigFactory

object Main extends App {

  val config = ConfigFactory.load("app.conf")

  private implicit val system = ActorSystem("xD", config)

  val parent = TestProbe("").ref

  testCheckout(Props(new CheckoutActor(IdProvider.newId(), parent)))

}

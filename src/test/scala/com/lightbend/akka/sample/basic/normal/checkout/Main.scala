package com.lightbend.akka.sample.basic.normal.checkout

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.testkit.TestProbe
import com.lightbend.akka.sample.basic.Commons._
import com.lightbend.akka.sample.basic.normal.checkout.CheckoutActor
import com.lightbend.akka.sample.basic.normal.payment.PaymentService
import com.lightbend.akka.sample.commonDefs.IdProvider
import com.typesafe.config.ConfigFactory

object Main extends App {

  val config = ConfigFactory.load("app.conf")

  private implicit val system: ActorSystem = ActorSystem("xD", config)

  val parent = TestProbe("").ref

  new PaymentService()(ActorMaterializer(), system)

  testCheckout(Props(new CheckoutActor(IdProvider.newId(), parent)))

}

package com.lightbend.akka.sample.basic.normal.checkout

import akka.actor.{ActorSystem, Props}
import com.lightbend.akka.sample.basic.Commons._
import com.lightbend.akka.sample.basic.fsm.checkout.CheckoutFSM

object Main extends App {

  private implicit val system = ActorSystem()

  testCheckout(Props(new CheckoutFSM))

}

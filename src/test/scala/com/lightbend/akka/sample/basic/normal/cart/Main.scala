package com.lightbend.akka.sample.basic.normal.cart

import akka.actor.{ActorSystem, Props}
import com.lightbend.akka.sample.basic.Commons._
import com.lightbend.akka.sample.basic.fsm.cart.CartFSM

object Main extends App {

  private implicit val system = ActorSystem()

  private val actor = system.actorOf(Props(new CartFSM))

  testCart(actor)

}

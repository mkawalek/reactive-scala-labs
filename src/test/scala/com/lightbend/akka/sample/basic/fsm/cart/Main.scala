package com.lightbend.akka.sample.basic.fsm.cart

import akka.actor.{ActorSystem, Props}
import com.lightbend.akka.sample.basic.Commons._

object Main extends App {

  private implicit val system = ActorSystem()

  private val actor = system.actorOf(Props(new CartFSM))

  testCart(actor)

}

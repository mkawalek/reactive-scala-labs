package com.lightbend.akka.sample.basic.normal.customer

import akka.actor.ActorRef

trait CustomerCommand

object CustomerCommands {

  final case class CheckoutStarted(checkout: ActorRef) extends CustomerCommand

  final case class PaymentServiceStarted(payment: ActorRef) extends CustomerCommand

  final case object PaymentConfirmed extends CustomerCommand

  final case object CartEmpty extends CustomerCommand

}

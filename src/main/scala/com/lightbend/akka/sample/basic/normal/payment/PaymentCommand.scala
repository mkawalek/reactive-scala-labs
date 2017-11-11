package com.lightbend.akka.sample.basic.normal.payment

trait PaymentCommand

object PaymentCommands {

  final case object DoPayment extends PaymentCommand

}

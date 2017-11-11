package com.lightbend.akka.sample.commonDefs

sealed trait CheckoutCommand

object CheckoutCommands {

  final case object DeliveryMethodSelected extends CheckoutCommand

  final case object Cancel extends CheckoutCommand

  final case object PaymentSelected extends CheckoutCommand

  final case object PaymentReceived extends CheckoutCommand

}

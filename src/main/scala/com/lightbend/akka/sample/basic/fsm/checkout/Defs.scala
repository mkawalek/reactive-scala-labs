package com.lightbend.akka.sample.basic.fsm.checkout

object Defs {

  sealed trait State

  final case object SelectingDelivery extends State

  final case object SelectingPaymentMethod extends State

  final case object ProcessingPayment extends State

  final case object Closed extends State

  final case object Cancelled extends State

  final case object PaymentTimerExpired

  final case object CheckoutTimerExpired

}

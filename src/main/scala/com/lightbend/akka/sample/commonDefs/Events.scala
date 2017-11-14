package com.lightbend.akka.sample.commonDefs

sealed trait CartEvent

sealed trait CheckoutEvent

object Events {

  final case class CartStateUpdated(cart: Cart) extends CartEvent

  case object CheckoutStartedEvent extends CartEvent


  case object CancelledEvent extends CheckoutEvent

  case object DeliveryMethodSelectedEvent extends CheckoutEvent

  case object CheckoutClosedEvent extends CheckoutEvent

  case object PaymentServiceStartedEvent extends CheckoutEvent

}

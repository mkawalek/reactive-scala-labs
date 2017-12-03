package com.lightbend.akka.sample.commonDefs

import akka.actor.ActorRef

sealed trait CartEvent

sealed trait CheckoutEvent

object Events {

  final case class CartStateUpdated(cart: Cart) extends CartEvent

  case object CheckoutStartedEvent extends CartEvent


  case object CancelledEvent extends CheckoutEvent

  case object DeliveryMethodSelectedEvent extends CheckoutEvent

  case object CheckoutClosedEvent extends CheckoutEvent

  case class PaymentServiceStartedEvent(actorRef: ActorRef) extends CheckoutEvent

}

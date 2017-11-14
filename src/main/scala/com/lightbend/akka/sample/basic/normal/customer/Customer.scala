package com.lightbend.akka.sample.basic.normal.customer

import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.basic.normal.cart.CartManager
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.{CheckoutStarted, PaymentConfirmed, PaymentServiceStarted}
import com.lightbend.akka.sample.basic.normal.payment.PaymentCommands.DoPayment
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import com.lightbend.akka.sample.commonDefs.Commands.{AddItem, StartCheckout}
import com.lightbend.akka.sample.commonDefs.Item

class Customer extends Actor {
  private val cart = context.actorOf(Props(new CartManager("2")))

  cart ! AddItem(Item("first"))
  cart ! StartCheckout

  override def receive = {
    case CheckoutStarted(checkout) =>
      checkout ! DeliveryMethodSelected
      checkout ! PaymentSelected
      context.become(payment(checkout))

  }

  def payment(checkout: ActorRef): Receive = {
    case PaymentServiceStarted(payment) => payment ! DoPayment
    case PaymentConfirmed => checkout ! PaymentReceived
  }


}

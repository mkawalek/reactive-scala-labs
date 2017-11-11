package com.lightbend.akka.sample.basic.normal.payment

import akka.actor.Actor
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.PaymentConfirmed
import com.lightbend.akka.sample.basic.normal.payment.PaymentCommands.DoPayment
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.PaymentReceived

class PaymentActor extends Actor {

  override def receive = {
    case DoPayment =>
      sender() ! PaymentConfirmed
      context.parent ! PaymentReceived
  }

}

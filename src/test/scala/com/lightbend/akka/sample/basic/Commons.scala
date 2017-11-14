package com.lightbend.akka.sample.basic

import akka.actor.{ActorRef, ActorSystem, Props}
import com.lightbend.akka.sample.commonDefs.Commands._
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import com.lightbend.akka.sample.commonDefs.{CheckoutCommands, Item}

object Commons {

  def testCart(actor: ActorRef) = {
    actor ! AddItem(Item("first item"))
    actor ! AddItem(Item("second item"))

    Thread sleep 4000

    actor ! AddItem(Item("third item"))
    actor ! AddItem(Item("fourth item"))

    actor ! DeleteItem(Item("third item"))
    actor ! DeleteItem(Item("fourth item"))

    actor ! AddItem(Item("fifth item"))
    actor ! AddItem(Item("sixth item"))

    actor ! StartCheckout

    actor ! CheckoutCancelled

    actor ! StartCheckout

    actor ! CheckoutClosed
  }

  def testCheckout(props: Props)(implicit system: ActorSystem) = {
    def newActor = system.actorOf(props)

    val success = newActor

    success ! DeliveryMethodSelected

    success ! PaymentSelected

    success ! PaymentReceived

    Thread sleep 1000

    newActor // checkoutTimerExpiration

    Thread sleep 4000

    val checkoutTimerExpiration = newActor

    checkoutTimerExpiration ! DeliveryMethodSelected

    Thread sleep 4000


    val paymentTimerExpiration = newActor

    paymentTimerExpiration ! DeliveryMethodSelected

    paymentTimerExpiration ! PaymentSelected

    Thread sleep 4000

    val cancelledAfterPaymentMethodSelected = newActor

    cancelledAfterPaymentMethodSelected ! DeliveryMethodSelected

    cancelledAfterPaymentMethodSelected ! CheckoutCommands.Cancel

    Thread sleep 4000

    val cancelledAfterPaymentSelected = newActor

    cancelledAfterPaymentSelected ! DeliveryMethodSelected

    cancelledAfterPaymentSelected ! PaymentSelected

    cancelledAfterPaymentSelected ! CheckoutCommands.Cancel
  }

}

package com.lightbend.akka.sample.basic.normal.checkout

import akka.actor.{Actor, ActorRef, Props, Timers}
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.PaymentServiceStarted
import com.lightbend.akka.sample.basic.normal.payment.PaymentActor
import com.lightbend.akka.sample.commonDefs.CartCommands.CheckoutClosed
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.{Cancel, DeliveryMethodSelected, PaymentReceived, PaymentSelected}

import scala.concurrent.duration._

class CheckoutActor(parent: ActorRef) extends Actor with Timers {

  override def receive = selectingDelivery

  private def selectingDelivery: Receive = {
    println("IN SELECTING DELIVERY")
    timers.startSingleTimer(CheckoutTimer, CheckoutTimerExpired, 3 seconds)


    {
      case DeliveryMethodSelected => context.become(selectingPaymentMethod)

      case CheckoutTimerExpired => context.become(cancelled)

      case Cancel => context.become(cancelled)
    }
  }

  private def selectingPaymentMethod: Receive = {
    println("IN SELECTING PAYMENT")

    {
      case PaymentSelected =>
        sender() ! PaymentServiceStarted(context.actorOf(Props(new PaymentActor())))
        context.become(processingPayment)

      case CheckoutTimerExpired => context.become(cancelled)

      case Cancel => context.become(cancelled)
    }
  }

  private def processingPayment: Receive = {
    println("IN PROCESSING PAYMENT")
    timers.cancel(CheckoutTimer)
    timers.startSingleTimer(PaymentTimer, PaymentTimerExpired, 3 seconds)

    {
      case PaymentReceived =>
        println("CLOSED")
        parent ! CheckoutClosed
        context.become(closed)

      case PaymentTimerExpired => context.become(cancelled)

      case Cancel => context.become(cancelled)
    }
  }

  private def closed: Receive = {
    println("IN CLOSED")
    timers.cancelAll()

    {
      case _ => // ignore ?
    }
  }

  private def cancelled: Receive = {
    println("IN CANCELLED")
    timers.cancelAll()

    {
      case _ => // ignore ?
    }
  }


  private final case object CheckoutTimer

  private final case object CheckoutTimerExpired

  private final case object PaymentTimer

  private final case object PaymentTimerExpired

}

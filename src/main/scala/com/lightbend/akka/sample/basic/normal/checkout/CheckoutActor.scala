package com.lightbend.akka.sample.basic.normal.checkout

import akka.actor.{ActorRef, Props, Timers}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.PaymentServiceStarted
import com.lightbend.akka.sample.basic.normal.payment.PaymentActor
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.{Cancel, DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import com.lightbend.akka.sample.commonDefs.CheckoutEvent
import com.lightbend.akka.sample.commonDefs.Commands.CheckoutClosed
import com.lightbend.akka.sample.commonDefs.Events._

import scala.concurrent.duration._

class CheckoutActor(id: String, parent: ActorRef) extends PersistentActor with Timers {

  override def receiveRecover = {
    case RecoveryCompleted => println("completed recovery")
    case event: CheckoutEvent => handleEvent(event)
  }

  override def receiveCommand = selectingDelivery

  override def persistenceId = id

  private def handleEvent(event: Any) = event match {
    case DeliveryMethodSelectedEvent => context.become(selectingPaymentMethod)

    case PaymentServiceStartedEvent => context.become(processingPayment)

    case CheckoutClosedEvent => context.become(closed)

    case CancelledEvent => context.become(cancelled)
  }

  private def selectingDelivery: Receive = {
    println("IN SELECTING DELIVERY")
    timers.startSingleTimer(CheckoutTimer, CheckoutTimerExpired, 3 seconds)

    {
      case DeliveryMethodSelected =>
        persist(DeliveryMethodSelectedEvent)(handleEvent)

      case CheckoutTimerExpired => persist(CancelledEvent)(handleEvent)

      case Cancel => persist(CancelledEvent)(handleEvent)
    }
  }

  private def selectingPaymentMethod: Receive = {
    println("IN SELECTING PAYMENT")

    {
      case PaymentSelected =>
        persist(PaymentServiceStartedEvent)(handleEvent)
        deferAsync(id)(_ => sender() ! PaymentServiceStarted(context.actorOf(Props(new PaymentActor()))))

      case CheckoutTimerExpired => persist(CancelledEvent)(handleEvent)

      case Cancel => persist(CancelledEvent)(handleEvent)
    }
  }

  private def processingPayment: Receive = {
    println("IN PROCESSING PAYMENT")
    timers.cancel(CheckoutTimer)
    timers.startSingleTimer(PaymentTimer, PaymentTimerExpired, 3 seconds)

    {
      case PaymentReceived =>
        println("CLOSED")
        persist(CheckoutClosedEvent)(handleEvent)
        deferAsync(id)(_ => parent ! CheckoutClosed)

      case PaymentTimerExpired => persist(CancelledEvent)(handleEvent)

      case Cancel => persist(CancelledEvent)(handleEvent)
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

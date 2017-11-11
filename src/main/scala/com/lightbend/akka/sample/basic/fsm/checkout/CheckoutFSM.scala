package com.lightbend.akka.sample.basic.fsm.checkout

import akka.Done
import akka.actor.FSM
import com.lightbend.akka.sample.commonDefs.CheckoutCommands
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import com.lightbend.akka.sample.basic.fsm.checkout.Defs.{Cancelled, _}

import scala.concurrent.duration._

class CheckoutFSM extends FSM[State, Done] {
  private final val checkoutTimer = "checkoutTimer"
  private final val paymentTimer = "paymentTimer"

  when(SelectingDelivery) {
    case Event(DeliveryMethodSelected, _) =>
      println("SELECTING PAYMENT METHOD")
      goto(SelectingPaymentMethod)

    case Event(CheckoutTimerExpired, _) =>
      println("CANCELLED")
      goto(Cancelled)

    case Event(CheckoutCommands.Cancel, _) =>
      println("CANCELLED")
      goto(Cancelled)
  }

  onTransition {
    case _ -> SelectingDelivery => setTimer(checkoutTimer, CheckoutTimerExpired, 3 seconds)
    case _ -> ProcessingPayment =>
      cancelTimer(checkoutTimer)
      setTimer(paymentTimer, PaymentTimerExpired, 3 seconds)
    case _ -> Cancelled =>
      cancelTimer(checkoutTimer)
      cancelTimer(paymentTimer)
    case _ -> Closed =>
      cancelTimer(checkoutTimer)
      cancelTimer(paymentTimer)
  }

  when(SelectingPaymentMethod) {
    case Event(PaymentSelected, _) =>
      println("PROCESSING PAYMENT")
      goto(ProcessingPayment)

    case Event(CheckoutTimerExpired, _) =>
      println("CANCELLED")
      goto(Cancelled)

    case Event(CheckoutCommands.Cancel, _) =>
      println("CANCELLED")
      goto(Cancelled)
  }

  when(ProcessingPayment) {
    case Event(PaymentReceived, _) =>
      println("CLOSED")
      goto(Closed)

    case Event(PaymentTimerExpired, _) =>
      println("CANCELLED")
      goto(Cancelled)

    case Event(CheckoutCommands.Cancel, _) =>
      println("CANCELLED")
      goto(Cancelled)
  }

  when(Closed) {
    case Event(e, _) =>
      log.warning("GOTO FROM CLOSED TO CLOSED")
      goto(Closed)
  }

  when(Cancelled) {
    case Event(e, _) =>
      log.warning("GOTO FROM CANCELLED TO CANCELLED")
      goto(Cancelled)
  }

  whenUnhandled {
    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  startWith(SelectingDelivery, Done)
  initialize()
}





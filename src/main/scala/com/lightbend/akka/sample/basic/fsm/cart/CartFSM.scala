package com.lightbend.akka.sample.basic.fsm.cart

import akka.actor.FSM
import com.lightbend.akka.sample.commonDefs.CartCommands._
import com.lightbend.akka.sample.basic.fsm.cart.Defs._

import scala.concurrent.duration._

class CartFSM extends FSM[State, CartContent] {
  private final val CartTimer = "cartTimer"

  when(Empty) {
    case Event(AddItem(item), EmptyCart) =>
      println("NON EMPTY", item)
      goto(NonEmpty) using CurrentItems(List(item))
  }

  onTransition {
    case _ -> NonEmpty => setTimer(CartTimer, CartTimerExpiration, 3 seconds)
    case _ -> (InCheckout | Empty) => cancelTimer(CartTimer)
  }

  when(NonEmpty) {
    case Event(AddItem(item), CurrentItems(items)) =>
      println("NON EMPTY", items :+ item)
      goto(NonEmpty) using CurrentItems(items :+ item)

    case Event(DeleteItem(item), CurrentItems(items)) =>
      val cleared = items.filterNot(_ == item)
      cleared.headOption.map(_ => println("NONEMPTY", cleared)).getOrElse(println("EMPTY"))
      cleared.headOption.map(_ => goto(NonEmpty) using CurrentItems(cleared)).getOrElse(goto(Empty) using EmptyCart)

    case Event(StartCheckout, items@CurrentItems(_)) =>
      println("IN CHECKOUT", items)
      goto(InCheckout) using items

    case Event(CartTimerExpiration, _) =>
      println("EMPTY")
      goto(Empty) using EmptyCart
  }

  when(InCheckout) {
    case Event(CheckoutCancelled, items@CurrentItems(_)) =>
      println("NONEMPTY", items)
      goto(NonEmpty) using items

    case Event(CheckoutClosed, _) =>
      println("EMPTY")
      goto(Empty) using EmptyCart
  }

  whenUnhandled {
    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  startWith(Empty, EmptyCart)
  initialize()
  println("EMPTY")
}



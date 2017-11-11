package com.lightbend.akka.sample.basic.normal.cart

import akka.actor.{Actor, Props, Timers}
import com.lightbend.akka.sample.basic.normal.checkout.CheckoutActor
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.{CartEmpty, CheckoutStarted}
import com.lightbend.akka.sample.commonDefs.CartCommands.{GetItems, _}
import com.lightbend.akka.sample.commonDefs.Item

import scala.concurrent.duration._

class CartActor extends Actor with Timers {
  override def receive = empty

  def empty: Receive = {
    println("IN EMPTY STATE")
    timers.cancelAll()

    {
      case AddItem(item) => context.become(nonEmpty(List(item)))

      case GetItems => sender() ! List.empty
    }
  }

  def nonEmpty(items: List[Item]): Receive = {
    println("IN NONEMPTY STATE", items)
    timers.startSingleTimer(CartTimer, CartTimerExpired, 3 seconds)

    {
      case AddItem(item) => context.become(nonEmpty(items :+ item))

      case DeleteItem(item) =>
        val cleared = items.filterNot(_ == item)
        context.become(cleared.headOption.map(_ => nonEmpty(cleared)).getOrElse(empty))

      case CartTimerExpired => context.become(empty)

      case StartCheckout =>
        sender() ! CheckoutStarted(context.actorOf(Props(new CheckoutActor(self))))
        context.become(inCheckout(items))

      case GetItems => sender() ! items
    }
  }

  def inCheckout(items: List[Item]): Receive = {
    println("IN CHECKOUT STATE")
    timers.cancelAll()

    {
      case CheckoutCancelled => context.become(nonEmpty(items))

      case CheckoutClosed =>
        context.parent ! CartEmpty
        context.become(empty)

      case GetItems => sender() ! items
    }
  }

  private final case object CartTimer

  private final case object CartTimerExpired

}

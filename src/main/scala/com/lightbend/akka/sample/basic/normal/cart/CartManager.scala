package com.lightbend.akka.sample.basic.normal.cart

import akka.actor.{Props, Timers}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.lightbend.akka.sample.basic.normal.checkout.CheckoutActor
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.{CartEmpty, CheckoutStarted}
import com.lightbend.akka.sample.commonDefs.{Cart, CartEvent, IdProvider}
import com.lightbend.akka.sample.commonDefs.Commands.{GetItems, _}
import com.lightbend.akka.sample.commonDefs.Events.{CartStateUpdated, CheckoutStartedEvent}

import scala.concurrent.duration._

class CartManager(id: String) extends PersistentActor with Timers {

  override def receiveRecover = {
    case RecoveryCompleted => println("RECOVERY COMPLETED")

    case event: CartEvent => handleEvent(event)
  }

  override def receiveCommand = empty

  override def persistenceId = s"card-manager-$id"

  var currentCart = Cart.empty

  private def handleEvent(event: CartEvent) = event match {
    case CartStateUpdated(newCart) =>
      currentCart = newCart
      if (newCart.items.isEmpty) context.become(empty)
      else context.become(nonEmpty)

    case CheckoutStartedEvent => context.become(inCheckout)
  }


  def empty: Receive = {
    println("IN EMPTY STATE")
    timers.cancelAll()

    {
      case AddItem(item) => persist(CartStateUpdated(currentCart.addItem(item)))(handleEvent)

      case GetItems => sender() ! List.empty
    }
  }

  def nonEmpty: Receive = {
    println("IN NONEMPTY STATE", currentCart)
    timers.startSingleTimer(CartTimer, CartTimerExpired, 3 seconds)

    {
      case AddItem(item) => persist(CartStateUpdated(currentCart.addItem(item)))(handleEvent)

      case DeleteItem(item) => persist(CartStateUpdated(currentCart.removeItem(item)))(handleEvent)

      case CartTimerExpired =>
        persist(CartStateUpdated(Cart.empty))(handleEvent)

      case StartCheckout =>
        persist(CheckoutStartedEvent)(handleEvent)
        deferAsync(id)(_ => sender() ! CheckoutStarted(context.actorOf(Props(new CheckoutActor(IdProvider.newId(), self)))))

      case GetItems => sender() ! currentCart.items
    }
  }

  def inCheckout: Receive = {
    println("IN CHECKOUT STATE")
    timers.cancelAll()

    {
      case CheckoutCancelled =>
        persist(CartStateUpdated(currentCart))(handleEvent)

      case CheckoutClosed =>
        persist(CartStateUpdated(Cart.empty))(handleEvent)
        deferAsync(id)(_ => context.parent ! CartEmpty)

      case GetItems => sender() ! currentCart.items
    }
  }

  private final case object CartTimer

  private final case object CartTimerExpired

}

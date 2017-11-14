package com.lightbend.akka.sample.cart

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.lightbend.akka.sample.basic.normal.cart.CartManager
import com.lightbend.akka.sample.commonDefs.Commands.{AddItem, DeleteItem, GetItems}
import com.lightbend.akka.sample.commonDefs.{IdProvider, Item}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class CartActorAsyncTests extends TestKit(ActorSystem("xD", ConfigFactory.load("app.conf"))) with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  override def afterAll(): Unit = {
    system.terminate
  }

  "Cart" should {
    "be empty at the very beginning" in {
      val cart = system.actorOf(Props(new CartManager(IdProvider.newId())))

      cart ! GetItems

      expectMsg(List.empty)
    }

    "have two items after addition" in {
      val cart = system.actorOf(Props(new CartManager(IdProvider.newId())))

      cart ! AddItem(Item("first"))

      cart ! AddItem(Item("second"))

      cart ! GetItems

      expectMsg(List(Item("first"), Item("second")))
    }

    "expire items after 3 seconds" in {
      val cart = system.actorOf(Props(new CartManager(IdProvider.newId())))

      cart ! AddItem(Item("first"))

      Thread sleep 4000

      cart ! GetItems

      expectMsg(List.empty)
    }

    "delete items from cart after such requests" in {
      val cart = system.actorOf(Props(new CartManager(IdProvider.newId())))

      cart ! AddItem(Item("first"))

      cart ! AddItem(Item("second"))

      cart ! GetItems

      expectMsg(List(Item("first"), Item("second")))

      cart ! DeleteItem(Item("first"))

      Thread sleep 500

      cart ! GetItems

      expectMsg(List(Item("second")))
    }

  }


}

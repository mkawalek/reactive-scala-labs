package com.lightbend.akka.sample.cart.checkout

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.lightbend.akka.sample.basic.normal.checkout.CheckoutActor
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.PaymentServiceStarted
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import com.lightbend.akka.sample.commonDefs.Commands.CheckoutClosed
import com.lightbend.akka.sample.commonDefs.IdProvider
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestKitCheckoutActorTests extends TestKit(ActorSystem("xD", ConfigFactory.load("app.conf"))) with ImplicitSender with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.terminate
  }

  "Checkout actor" must {
    "send CheckoutClosed to CartActor" in {
      val mockedCart = TestProbe()
      val checkoutActor = mockedCart.childActorOf(Props(new CheckoutActor(IdProvider.newId(), mockedCart.ref)))

      checkoutActor ! DeliveryMethodSelected

      checkoutActor ! PaymentSelected

      expectMsgType[PaymentServiceStarted]

      checkoutActor ! PaymentReceived

      mockedCart.expectMsg(CheckoutClosed)
    }

    "recover after system failure" in {
      val id = IdProvider.newId()
      val mockedCart = TestProbe()
      val checkoutActor = mockedCart.childActorOf(Props(new CheckoutActor(id, mockedCart.ref)))

      checkoutActor ! DeliveryMethodSelected

      checkoutActor ! PaymentSelected

      Thread sleep 1000

      println("TERMINATING")

      system.terminate()

      val newSystem = ActorSystem("xD2", ConfigFactory.load("app.conf"))
      val newMockedCart = TestProbe()(newSystem)

      val newCheckoutActor = newMockedCart.childActorOf(Props(new CheckoutActor(id, mockedCart.ref)))

      newCheckoutActor ! PaymentReceived

      Thread sleep 5000

      // should be closed, not cancelled !!!

    }

    "recover after system failure 2" in {
      val id = IdProvider.newId()
      val mockedCart = TestProbe()
      val checkoutActor = mockedCart.childActorOf(Props(new CheckoutActor(id, mockedCart.ref)))

      checkoutActor ! DeliveryMethodSelected

      Thread sleep 1000

      println("TERMINATING")

      system.terminate()

      val newSystem = ActorSystem("xD2", ConfigFactory.load("app.conf"))
      val newMockedCart = TestProbe()(newSystem)

      val newCheckoutActor = newMockedCart.childActorOf(Props(new CheckoutActor(id, mockedCart.ref)))

      Thread sleep 5000

      // should be cancelled but not after checkout timer but after payment timer

    }

  }

}

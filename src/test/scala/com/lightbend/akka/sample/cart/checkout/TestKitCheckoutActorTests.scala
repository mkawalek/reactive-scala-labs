package com.lightbend.akka.sample.cart.checkout

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.lightbend.akka.sample.basic.normal.checkout.CheckoutActor
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.PaymentServiceStarted
import com.lightbend.akka.sample.commonDefs.CartCommands.CheckoutClosed
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.{DeliveryMethodSelected, PaymentReceived, PaymentSelected}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestKitCheckoutActorTests extends TestKit(ActorSystem()) with ImplicitSender with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.terminate
  }

  "Checkout actor" must {
    "send CheckoutClosed to CartActor" in {
      val mockedCart = TestProbe()
      val checkoutActor = mockedCart.childActorOf(Props(new CheckoutActor(mockedCart.ref)))

      checkoutActor ! DeliveryMethodSelected

      checkoutActor ! PaymentSelected

      expectMsgType[PaymentServiceStarted]

      checkoutActor ! PaymentReceived

      mockedCart.expectMsg(CheckoutClosed)
    }
  }

}

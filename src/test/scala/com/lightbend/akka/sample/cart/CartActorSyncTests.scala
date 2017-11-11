package com.lightbend.akka.sample.cart

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.lightbend.akka.sample.basic.normal.cart.CartActor
import com.lightbend.akka.sample.commonDefs.CartCommands.{AddItem, DeleteItem, GetItems}
import com.lightbend.akka.sample.commonDefs.Item
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class CartActorSyncTests extends FlatSpec with Matchers {

  private implicit val system = ActorSystem()
  private implicit val ec = system.dispatcher
  private implicit val timeout = Timeout(5 seconds)

  "Cart Actor" should "work as expected" in {

    val actorRef = TestActorRef(new CartActor)

    val firstResult = (actorRef ? GetItems).mapTo[List[Item]]

    val firstSuccess = firstResult.value.get.getOrElse(throw new IllegalStateException("Future failed"))

    firstSuccess shouldBe empty

    actorRef ! AddItem(Item("first"))

    actorRef ! AddItem(Item("second"))

    val secondResult = (actorRef ? GetItems).mapTo[List[Item]]

    val secondSuccess = secondResult.value.get.getOrElse(throw new IllegalStateException("Future failed"))

    secondSuccess shouldBe List(Item("first"), Item("second"))

    Thread sleep 4000

    val thirdResult = (actorRef ? GetItems).mapTo[List[Item]]

    val thirdSuccess = thirdResult.value.get.getOrElse(throw new IllegalStateException("Future failed"))

    thirdSuccess shouldBe empty

    actorRef ! AddItem(Item("third"))

    actorRef ! AddItem(Item("fourth"))

    val fourthResult = (actorRef ? GetItems).mapTo[List[Item]]

    val fourthSuccess: List[Item] = fourthResult.value.get.getOrElse(throw new IllegalStateException("Future failed"))

    fourthSuccess shouldBe List(Item("third"), Item("fourth"))

    actorRef ! DeleteItem(Item("third"))

    val fifthResult = (actorRef ? GetItems).mapTo[List[Item]]

    val fifthSuccess: List[Item] = fifthResult.value.get.getOrElse(throw new IllegalStateException("Future failed"))

    fifthSuccess shouldBe List(Item("fourth"))

  }
}

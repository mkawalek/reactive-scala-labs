package com.lightbend.akka.sample.cart

import com.lightbend.akka.sample.commonDefs.{Cart, Item}
import org.scalatest.{FlatSpec, Matchers}

class CartTests extends FlatSpec with Matchers {

  "Cart" should "add item to itself" in {
    val cart = Cart.empty

    cart.addItem(Item("first")).addItem(Item("second")).items shouldBe List(Item("first"), Item("second"))
  }

  it should "remove item from itself" in {
    val cart = Cart(List(Item("first"), Item("second")))

    cart.removeItem(Item("first")).items shouldBe List(Item("second"))
  }

}

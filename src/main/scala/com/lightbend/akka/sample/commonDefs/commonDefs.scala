package com.lightbend.akka.sample

package object commonDefs {

  final case class Item(name: String)

  final case class Cart(items: List[Item]) {
    def addItem(item: Item) = this.copy(items = items :+ item)

    def removeItem(item: Item) = this.copy(items = items.filter(_ != item))
  }

  object Cart {
    def empty = Cart(List.empty)
  }

}

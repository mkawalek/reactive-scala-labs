package com.lightbend.akka.sample.commonDefs

sealed trait CartCommand

object CartCommands {

  final case class AddItem(item: Item) extends CartCommand

  final case class DeleteItem(item: Item) extends CartCommand

  final case object StartCheckout extends CartCommand

  final case object CheckoutCancelled extends CartCommand

  final case object CheckoutClosed extends CartCommand

  final case object GetItems extends CartCommand


}


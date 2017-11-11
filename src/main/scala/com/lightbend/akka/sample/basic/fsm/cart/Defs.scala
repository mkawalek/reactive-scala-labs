package com.lightbend.akka.sample.basic.fsm.cart

import com.lightbend.akka.sample.commonDefs.Item

object Defs {

  sealed trait State

  final case object Empty extends State

  final case object NonEmpty extends State

  final case object InCheckout extends State

  sealed trait CartContent

  final case object EmptyCart extends CartContent

  final case class CurrentItems(items: List[Item]) extends CartContent

  final case object CartTimerExpiration
}

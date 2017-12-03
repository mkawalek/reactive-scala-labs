package com.lightbend.akka.sample.basic.normal

import akka.actor.Actor

class AnswerActor extends Actor {
  override def receive = {
    case _ => sender() ! "OK"
  }
}

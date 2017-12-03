package com.lightbend.akka.sample.router

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.routing._
import com.lightbend.akka.sample.basic.normal.AnswerActor
import akka.pattern._
import akka.util.Timeout
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import com.lightbend.akka.sample.cluster.ClusterUtils

import scala.concurrent.duration._

class WorkRouter extends Actor {
  val numberOfWorkers = 5
  val router = Router(RoundRobinRoutingLogic(), (1 to numberOfWorkers).map(_ => ActorRefRoutee(context.actorOf(Props[Worker]))))

  override def receive = {
    case message =>
      println("IN WORK ROUTER")
      router.route(message, sender)
  }
}

class Worker extends Actor {
  private implicit val ec = context.dispatcher
  private implicit val timeout = Timeout(5 seconds)

  val proxy = context.actorOf(Props[AnswerActor])

  val publisher = DistributedPubSub(context.system).mediator

  override def receive = {
    case message =>
      println("IN WORKER")
      publisher ! Publish("requests", ClusterUtils.PORT)
      proxy ? message pipeTo sender
  }
}

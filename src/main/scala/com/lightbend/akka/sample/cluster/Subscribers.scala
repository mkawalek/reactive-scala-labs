package com.lightbend.akka.sample.cluster

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.lightbend.akka.sample.cluster.Subscribers.GetRequests
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Subscribers extends App with Json4sSupport {

  private val config = ConfigFactory.load("cluster.conf")

  private implicit val system = ActorSystem("cluster", config)
  private implicit val mat = ActorMaterializer()
  private implicit val ec = system.dispatcher
  private implicit val scheduler = system.scheduler
  private implicit val timeout = Timeout(5 seconds)

  final case class GetRequests(node: Int)

  private implicit val serialization = Serialization
  private implicit val formats = DefaultFormats

  System.setProperty("PORT", "7777")

  ClusterUtils.clusterRegion()

  val counter = system.actorOf(Props[RequestCounter])
  val printer = system.actorOf(Props[RequestsPrinter])


  val routes = (path("requests" / IntNumber) & get) { node =>
    onSuccess(counter ? GetRequests(node))(requests => complete(OK, Map("requests" -> requests)))
  }

  Http().bindAndHandle(routes, "0.0.0.0", 9999).onComplete {
    case Success(_) => println("API started on port 9999")
    case Failure(ex) => println("Error when starting api", ex)
  }

}

class RequestsPrinter extends Actor with ActorLogging {

  val subscriber = DistributedPubSub(context.system).mediator
  subscriber ! Subscribe("requests", self)

  override def receive = {
    case SubscribeAck(Subscribe("requests", None, `self`)) ⇒
      log.info("subscribing")

    case message => log.info("Request handled on server: {}", message)
  }
}

class RequestCounter extends Actor with ActorLogging {
  var requestsFrom4444 = 0
  var requestsFrom5555 = 0
  var requestsFrom6666 = 0

  val subscriber: ActorRef = DistributedPubSub(context.system).mediator

  subscriber ! Subscribe("requests", self)

  override def receive = {
    case GetRequests(node) if node == 4444 => sender() ! requestsFrom4444
    case GetRequests(node) if node == 5555 => sender() ! requestsFrom5555
    case GetRequests(node) if node == 6666 => sender() ! requestsFrom6666

    case message: Int if message == 4444 => requestsFrom4444 += 1
    case message: Int if message == 5555 => requestsFrom5555 += 1
    case message: Int if message == 6666 => requestsFrom6666 += 1

    case SubscribeAck(Subscribe("requests", None, `self`)) =>
      log.info("subscribing...")

    case message => println("UNHANDLED ", message)
  }

}





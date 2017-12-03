package com.lightbend.akka.sample.basic.normal.payment

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.pattern.CircuitBreaker
import akka.stream.ActorMaterializer
import com.lightbend.akka.sample.basic.normal.customer.CustomerCommands.PaymentConfirmed
import com.lightbend.akka.sample.basic.normal.payment.PaymentCommands.DoPayment
import com.lightbend.akka.sample.commonDefs.CheckoutCommands.PaymentReceived

import scala.concurrent.duration._
import scala.util.Random

class PaymentActor extends Actor {
  private implicit val ec = context.dispatcher
  private implicit val s = context.system
  private implicit val mat = ActorMaterializer()

  val cardProviders = List("visa", "paypal", "payu")

  private val circuitBreaker = CircuitBreaker.apply(
    s.scheduler,
    3,
    3 seconds,
    3 seconds
  )
    .onClose(println("CB CLOSED"))
    .onOpen(println("CB OPEN"))
    .onHalfOpen(println("HALF OPEN"))


  override def receive = {
    case DoPayment =>
      circuitBreaker.withCircuitBreaker(Http()
        .singleRequest(HttpRequest(uri = s"http://0.0.0.0:5555/${Random.shuffle(cardProviders).head}"))
        .map(response => {
          if (response.status.isSuccess()) sender() ! PaymentConfirmed
//          else sender() ! PaymentConfirmed

          context.parent ! PaymentReceived
        })
        .recover { case ex: Throwable => println("Error when trying to use rest payment service", ex) }
      )
  }

}

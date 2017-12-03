package com.lightbend.akka.sample.basic.normal.payment

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.{Http, server}
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class PaymentService(implicit mat: ActorMaterializer, system: ActorSystem) extends Json4sSupport {

  private implicit val formats = DefaultFormats
  private implicit val serialization = Serialization

  val route: server.Route =
    path("visa") {
      get {
        println("RECEIVED REQUEST FOR VISA !!!")
        complete(OK)
      }
    } ~
      path("paypal") {
        get {
          println("RECEIVED REQUEST FOR PAYPAL !!!")
          complete(OK)
        }
      } ~
      path("payu") {
        get {
          println("RECEIVED REQUEST FOR PAYU !!!")
          complete(OK)
        }
      }

  Http().bindAndHandle(route, "0.0.0.0", 5555)

}

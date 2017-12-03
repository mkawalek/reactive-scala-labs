package com.lightbend.akka.sample.router

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

import scala.util.Failure
import scala.util.Success

import scala.concurrent.duration._

object Main extends App with Json4sSupport {
  private implicit val system = ActorSystem()
  private implicit val mat = ActorMaterializer()
  private implicit val ec = system.dispatcher
  private implicit val scheduler = system.scheduler
  private implicit val timeout = Timeout(5 seconds)

  val router = system.actorOf(Props[WorkRouter])

  private implicit val serialization = Serialization
  private implicit val formats = DefaultFormats

  val routes = (path("job") & get) {
    println("IN ENDPOINT")
    onSuccess(router ? "job")(complete(OK, _))
  }

  Http().bindAndHandle(routes, "localhost", 8080).onComplete {
    case Success(_) => println("API started on port 8080")
    case Failure(ex) => println("Error when starting api", ex)
  }

}

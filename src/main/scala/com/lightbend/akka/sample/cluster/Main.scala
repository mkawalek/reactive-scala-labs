package com.lightbend.akka.sample.cluster

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import org.json4s.native.Serialization
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.json4s.DefaultFormats
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.pattern.ask
import com.lightbend.akka.sample.cluster.ClusterUtils.EntityEnvelope
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpjson4s.Json4sSupport

import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

object Main extends App with Json4sSupport {
  private val config = ConfigFactory.load("cluster.conf")

  private implicit val system = ActorSystem("cluster", config)
  private implicit val mat = ActorMaterializer()
  private implicit val ec = system.dispatcher
  private implicit val scheduler = system.scheduler
  private implicit val timeout = Timeout(5 seconds)

  private val cluster = ClusterUtils.clusterRegion()

  private implicit val serialization = Serialization
  private implicit val formats = DefaultFormats

  val routes = (path("job") & get) {
    println("IN ENDPOINT")
    onSuccess(cluster ? EntityEnvelope(Random.nextInt(1000)))(complete(OK, _))
  }

  Http().bindAndHandle(routes, "0.0.0.0", ClusterUtils.PORT).onComplete {
    case Success(_) => println(s"API started on port ${ClusterUtils.PORT}")
    case Failure(ex) => println("Error when starting api", ex)
  }

}

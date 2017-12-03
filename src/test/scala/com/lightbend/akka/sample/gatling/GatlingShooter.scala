package com.lightbend.akka.sample.gatling

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

class GatlingShooter extends Simulation with StrictLogging {

  private implicit val testSystem: ActorSystem = ActorSystem()
  private implicit val testActorMaterializer: ActorMaterializer = ActorMaterializer()
  private implicit val scheduler: Scheduler = testSystem.scheduler
  private implicit val ec: ExecutionContextExecutor = testSystem.dispatcher
  private implicit val timeout: Timeout = Timeout(5 seconds)

  private val httpProtocol = http
    .baseURL("http://localhost:8080")
    .acceptHeader("""text/html,application/xhtml+xml,application/json,application/xml;q=0.9,*/*;q=0.8""")

  private val basicScenario = scenario("Facebook Chatbot scenario")
    .exec(http("testMessage")
      .get("/job")
      .check(status is 200)
    )

  setUp(basicScenario.inject(
    constantUsersPerSec(10) during (5 seconds)
  ).protocols(httpProtocol))

}

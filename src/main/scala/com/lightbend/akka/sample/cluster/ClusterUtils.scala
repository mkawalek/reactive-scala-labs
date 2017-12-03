package com.lightbend.akka.sample.cluster

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.lightbend.akka.sample.router.WorkRouter

object ClusterUtils {
  val PORT: Int = System.getProperty("PORT").toInt

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case EntityEnvelope(id) ⇒ (id.toString, "Some Message xD")
  }

  val numberOfShards = 30

  val extractShardId : ShardRegion.ExtractShardId = {
    case EntityEnvelope(id) => (id % numberOfShards).toString
  }

  final case class EntityEnvelope(id: Long)

  def clusterRegion()(implicit system: ActorSystem): ActorRef = ClusterSharding(system).start(
    typeName = "Counter",
    entityProps = Props[WorkRouter],
    settings = ClusterShardingSettings(system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId)

}

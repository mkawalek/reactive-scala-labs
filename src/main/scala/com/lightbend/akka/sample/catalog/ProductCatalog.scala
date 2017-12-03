package com.lightbend.akka.sample.catalog

import java.nio.file.Paths

import akka.actor.Actor
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing, Sink}
import akka.util.ByteString
import com.lightbend.akka.sample.catalog.CatalogCommands.{GetByBrand, GetBySearch}
import com.lightbend.akka.sample.catalog.ProductCatalog.{Catalog, CatalogItem, LoadedCatalog}
import com.lightbend.akka.sample.commonDefs.Item

class ProductCatalog() extends Actor {
  implicit val ec = context.dispatcher
  implicit val mat = ActorMaterializer()
  private val catalogPath = Paths.get("src/main/resources/query_result")

  override def receive: Receive = {
    readCatalog()
    servingPoints(Nil, Nil)
  }

  def servingPoints(items: List[CatalogItem], items2: List[Item]): Receive = {
    case LoadedCatalog(catalog) =>
      println("LOADED")
      context.become(servingPoints(catalog.items, catalog.items.map(i => Item(i.name))))

    case GetBySearch(search) =>
      println("ELO")
      val result = items.map { catalogItem =>
        val words = search.split(" ")
        (catalogItem, words.count(catalogItem.name.contains(_)))
      }.sortBy(_._2).takeRight(10).reverse

      sender() ! result

    case GetByBrand(brand) => sender() ! items.filter(_.brand == brand)
  }

  private def readCatalog() = {
    FileIO.fromPath(catalogPath)
      .via(Framing.delimiter(
        ByteString("\n"),
        maximumFrameLength = 10000,
        allowTruncation = true))
      .map(_.utf8String)
      .map(line => {
        val split = line.split(",").map(_.replaceAll("\"", ""))
        CatalogItem(split.headOption.getOrElse(""), split.lift(1).getOrElse(""), split.lift(2).getOrElse(""))
      })
      .runWith(Sink.seq)
      .map(_.toList)
      .map(Catalog)
      .map(LoadedCatalog)
      .recover { case ex: Throwable => println("ERROR", ex) }
      .pipeTo(self)

  }


}

object ProductCatalog {

  final case class Catalog(items: List[CatalogItem])

  final case class CatalogItem(ean: String, name: String, brand: String)

  final case class LoadedCatalog(catalog: Catalog)

}

object CatalogCommands {

  final case class GetBySearch(search: String)

  final case class GetByBrand(brand: String)

}

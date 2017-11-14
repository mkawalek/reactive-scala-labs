package com.lightbend.akka.sample.commonDefs

import java.util.UUID

object IdProvider {

  def newId() = UUID.randomUUID().toString.replaceAll("-", "")

}

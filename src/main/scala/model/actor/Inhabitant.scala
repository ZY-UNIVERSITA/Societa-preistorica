package com.zy.societapreistorica
package model.actor

import akka.actor.{Actor, ActorRef}

class Inhabitant(name: String, resourceManager: ActorRef) extends Actor {
  private var resources: Map[String, Int] = Map(
    "water" -> 0,
    "wood" -> 0
  )

  override def preStart(): Unit = {
    super.preStart()

    resourceManager ! GetResource(name, "water")
    resourceManager ! GetResource(name, "wood")
  }

  override def receive: Receive = {
    case ReceiveResource(resourceType, value) => {

      val resourceValue = resources(resourceType)

      resources = resources.updated(resourceType, resourceValue + value)
      println(s"${name} got ${value} units of ${resourceType}. Now has: ${resources(resourceType)} units")
    }
  }
}

package com.zy.societapreistorica
package model

import akka.actor.Actor

import scala.collection.immutable.Queue

case class GetResource(inhabitantName: String, resourceType: String)
case class ReplenishResource(resourceType: String, value: Int)
case class ReceiveResource(resourceType: String, value: Int)


class Resources extends Actor {
  private var resources: Map[String, Int] = Map(
    "water" -> 2,
    "wood" -> 2
  )

  private var queue: Map[String, Queue[ResourceQueue]] = Map(
    "water" -> Queue(),
    "wood" -> Queue()
  )

  def receive: Receive = {
    case GetResource(inhabitantName, resourceType) =>
      val resourceValue = resources(resourceType)
      val senderActor = sender()

      if (resourceValue > 0) {
        resources = resources.updated(resourceType, resourceValue - 1)

        senderActor ! ReceiveResource(resourceType, 1)
        println(s"$resourceType has been pulled out by $inhabitantName. Remaining: ${resources(resourceType)}")
      } else {
        queue = queue.updated(resourceType, queue(resourceType).enqueue(ResourceQueue(senderActor, inhabitantName)))

        println(s"${inhabitantName} tried to get some ${resourceType}. Not enough water: ${resources(resourceType)}")
      }

    case ReplenishResource(resourceType, value) =>
      println(s"${resourceType} has been replenished by $value unit.")

      var newValue = value

      if (queue(resourceType).nonEmpty) {
        val (nextActor, newQueue) = queue(resourceType).dequeue
        queue = queue.updated(resourceType, newQueue)

        newValue -= 1
        nextActor.actorRef ! ReceiveResource(resourceType, 1)

        println(s"${resourceType} has been pulled out by ${nextActor.actorName}. Remaining: ${resources(resourceType)}")
      }

      if (newValue > 0) {
        val resourceValue = resources(resourceType)

        resources = resources.updated(resourceType, resourceValue + value)
        println(s"${resourceType.toUpperCase()} has been replenished. Remaining: ${resources(resourceType)}")
      }
  }
}

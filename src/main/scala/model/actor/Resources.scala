package com.zy.societapreistorica
package model.actor

import model.ResourceQueue

import akka.actor.Actor

import scala.collection.immutable.Queue

sealed trait ResourceEvent
case class GetResource(inhabitantName: String, resourceType: String) extends ResourceEvent
case class ReplenishResource(resourceType: String, value: Int) extends ResourceEvent
case class LoseResource(resourceType: String, value: Int) extends ResourceEvent
case class ReceiveResource(resourceType: String, value: Int) extends ResourceEvent
case class NothingResource() extends ResourceEvent

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
      replenishResource(resourceType, value)

    case LoseResource(resourceType, value) =>

      val resourceValue = resources(resourceType)

      if (resourceValue > 0) {
        println(s"${math.max(0, resourceValue - value)}")
        resources = resources.updated(resourceType, math.max(0, resourceValue - value))
      }

      println(s"Lost ${value.abs} unit of $resourceType. Remaining: ${resources(resourceType)}")

    case _ => println(s"Nothing happened.")
  }

  private def replenishResource(resourceType: String, value: Int): Unit = {
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
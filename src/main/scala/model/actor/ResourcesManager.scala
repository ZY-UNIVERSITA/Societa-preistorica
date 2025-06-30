package com.zy.societapreistorica
package model.actor

import model.entities.ResourcesEnum.{FOOD, WATER, WOOD}
import model.entities.{ResourceQueue, ResourcesEnum}
import model.traits.*

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable.Queue

object ResourcesManager {
  def apply(): Behavior[ResourcesRequests] = {
    Behaviors.setup { context =>
      val resources: Map[ResourcesEnum, Int] = Map(
        WATER -> 2,
        WOOD -> 2,
        FOOD -> 3
      )

      val queue: Map[ResourcesEnum, Queue[ResourceQueue]] = Map(
        WATER -> Queue.empty,
        WOOD -> Queue.empty,
        FOOD -> Queue.empty
      )
      behavior(resources, queue)
    }
  }

  private def behavior(resources: Map[ResourcesEnum, Int], queue: Map[ResourcesEnum, Queue[ResourceQueue]]): Behavior[ResourcesRequests] =
    Behaviors.receive { (context, message) =>
      var newResources: Map[ResourcesEnum, Int] = resources
      var newQueue: Map[ResourcesEnum, Queue[ResourceQueue]] = queue

      message match {
        case RequestResource(sender, resourceName, quantity) =>
          val requestedResourcesRemainingQuantity = resources(resourceName)

          if (requestedResourcesRemainingQuantity > 0 && requestedResourcesRemainingQuantity >= quantity) {
            newResources = resources.updated(resourceName, requestedResourcesRemainingQuantity - quantity)
            sender ! ReceiveResource(resourceName, quantity)

          } else {
            newQueue = queue.updated(resourceName, queue(resourceName).enqueue(ResourceQueue(sender, quantity)))
            sender ! NegativeResourceMessage(resourceName)
          }

        case ReplenishResource(resourceName, quantity) =>
          val newQuantity = resources(resourceName) + quantity

          if (queue(resourceName).nonEmpty) {
            val resourceQueue = queue(resourceName)
            val needQuantity = resourceQueue.head.quantity

            if (newQuantity >= needQuantity) {
              val (head, newResourceQueue) = resourceQueue.dequeue

              if ((newQuantity - needQuantity) > 0) {
                newResources = resources.updated(resourceName, newQuantity - needQuantity)
              }

              newQueue = queue.updated(resourceName, newResourceQueue)

              context.log.info(s"${resourceName} has been replenished by ${quantity} unit.")
              head.actorRef ! ReceiveResource(resourceName, needQuantity)
            }
          }

        case LoseResource(resourceName, quantity) => {
          val oldQuantity = resources(resourceName)
          val newQuantity = Math.max(0, oldQuantity-quantity)

          newResources = resources.updated(resourceName, newQuantity)

          context.log.info(s"Lost $quantity unit of $resourceName.")
        }
      }

      behavior(newResources, newQueue)
    }
}

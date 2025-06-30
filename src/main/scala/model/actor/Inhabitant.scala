package com.zy.societapreistorica
package model.actor

import model.entities.ResourcesEnum
import model.entities.ResourcesEnum.WATER
import model.traits.*

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object Inhabitant {
  def apply(inhabitantName: String, resourceManager: ActorRef[ResourcesRequests]): Behavior[InhabitantMessages] =
    Behaviors.setup { context =>
      resourceManager ! RequestResource(context.self, WATER, 1)

      behavior(inhabitantName, Map.empty)
    }

  private def behavior(inhabitantName: String, resources: Map[ResourcesEnum, Int]): Behavior[InhabitantMessages] =
    Behaviors.receive { (context, message) =>
      message match {
        case ReceiveResource(resourceName, quantity) =>
          val currentResourceQuantity = resources.getOrElse(resourceName, 0)
          val newResourceQuantity = resources.updated(resourceName, currentResourceQuantity + quantity)

          context.log.info(s"${inhabitantName.toUpperCase}: received $quantity unit of $resourceName")

          behavior(inhabitantName, newResourceQuantity)

        case NegativeResourceMessage(resourceName) =>
          context.log.info(s"${inhabitantName.toUpperCase}: ${resourceName} is not available.")

          behavior(inhabitantName, resources)
      }
    }
}

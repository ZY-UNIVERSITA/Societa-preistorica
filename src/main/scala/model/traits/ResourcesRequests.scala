package com.zy.societapreistorica
package model.traits

import akka.actor.typed.ActorRef
import com.zy.societapreistorica.model.entities.ResourcesEnum

sealed trait ResourcesRequests

case class RequestResource(sender: ActorRef[InhabitantMessages], resourceName: ResourcesEnum, quantity: Int) extends ResourcesRequests
case class ReplenishResource(resourceName: ResourcesEnum, quantity: Int) extends ResourcesRequests
case class LoseResource(resourceName: ResourcesEnum, quantity: Int) extends ResourcesRequests
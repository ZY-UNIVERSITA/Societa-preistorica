package com.zy.societapreistorica
package model.entities

import model.traits.InhabitantMessages

import akka.actor.typed.ActorRef

case class ResourceQueue(actorRef: ActorRef[InhabitantMessages], quantity: Int)

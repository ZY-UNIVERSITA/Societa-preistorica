package com.zy.societapreistorica
package model

import akka.actor.ActorRef

case class ResourceQueue(actorRef: ActorRef, actorName: String)

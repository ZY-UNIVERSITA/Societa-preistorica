package com.zy.societapreistorica
package model

import akka.actor.{Actor, ActorRef}

class Inhabitant(name: String, resource: ActorRef) extends Actor {
  override def preStart(): Unit = {
    resource ! GetResource(name, "water")
  }

  override def receive: Receive = {
    case _ => println("oki")
  }
}

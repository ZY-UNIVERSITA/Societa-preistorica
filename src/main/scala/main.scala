package com.zy.societapreistorica

import model.{Inhabitant, ReplenishResource, Resources}

import akka.Done
import akka.actor.{Actor, ActorSystem, CoordinatedShutdown, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@main
def main(): Unit =
  val system = ActorSystem("Tribe")

  val resourcesManager = system.actorOf(Props[Resources](), "Resources")

  val inhabitant1 = system.actorOf(Props(new Inhabitant("Marco", resourcesManager)), "Marco")
  val inhabitant2 = system.actorOf(Props(new Inhabitant("Luca", resourcesManager)), "Luca")
  val inhabitant3 = system.actorOf(Props(new Inhabitant("Lucia", resourcesManager)), "Lucia")

  Future {
    Thread.sleep(5000)
    resourcesManager ! ReplenishResource("water", 3)

    Thread.sleep(2500)
    resourcesManager ! ReplenishResource("wood", 3)

    Thread.sleep(2000)
    sys.exit(0)
  }
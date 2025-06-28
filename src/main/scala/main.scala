package com.zy.societapreistorica

import akka.actor.{Actor, ActorSystem, Props}
import com.zy.societapreistorica.model.{Inhabitant, Resources}


@main
def main(): Unit =
  val system = ActorSystem("Tribe")

  val resourcesManager = system.actorOf(Props[Resources](), "Resources")

  val inhabitant1 = system.actorOf(Props(new Inhabitant("Marco", resourcesManager)), "Marco")
  val inhabitant2 = system.actorOf(Props(new Inhabitant("Luca", resourcesManager)), "Luca")
  val inhabitant3 = system.actorOf(Props(new Inhabitant("Lucia", resourcesManager)), "Lucia")
package com.zy.societapreistorica

import model.actor.{Environment, Inhabitant, Resources}

import akka.actor.{ActorSystem, Props}

@main
def main(): Unit =
  val system = ActorSystem("Tribe")

  val resourcesManager = system.actorOf(Props[Resources](), "Resources")

  val inhabitant1 = system.actorOf(Props(new Inhabitant("Marco", resourcesManager)), "Marco")
  val inhabitant2 = system.actorOf(Props(new Inhabitant("Luca", resourcesManager)), "Luca")
  val inhabitant3 = system.actorOf(Props(new Inhabitant("Lucia", resourcesManager)), "Lucia")

  val environment = system.actorOf(Props(new Environment(resourcesManager)), "Environment")

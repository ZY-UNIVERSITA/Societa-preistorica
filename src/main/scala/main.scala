package com.zy.societapreistorica

import model.actor.{EnvironmentManager, Inhabitant, ResourcesManager}
import model.traits.ReplenishResource

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.zy.societapreistorica.model.entities.ResourcesEnum.{FOOD, WATER, WOOD}

@main
def main(): Unit = {
  val rootBehavior = Behaviors.setup[Nothing] { context =>
    val resourcesManager = context.spawn(ResourcesManager(), "Resources")

    val inhabitant1 = context.spawn(Inhabitant("Marco", resourcesManager), "Marco")
    val inhabitant2 = context.spawn(Inhabitant("Luca", resourcesManager), "Luca")
    val inhabitant3 = context.spawn(Inhabitant("Lucia", resourcesManager), "Lucia")

    val environmentManager = context.spawn(EnvironmentManager(resourcesManager), "Environment")

    Behaviors.empty
  }

  val system = ActorSystem[Nothing](rootBehavior, "Tribe")

}


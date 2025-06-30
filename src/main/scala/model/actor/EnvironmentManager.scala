package com.zy.societapreistorica
package model.actor

import model.traits.ResourcesRequests
import model.utils.EnvironmentalEventModifier

import akka.actor.Cancellable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop}

import scala.concurrent.duration.*
import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

sealed trait EnvironmentMessage

case class TimeTicker(tickType: String) extends EnvironmentMessage

sealed trait EnvironmentEvent

case object Rain extends EnvironmentEvent

case object Drought extends EnvironmentEvent

case object Clear extends EnvironmentEvent

case class RandomEvent(event: EnvironmentEvent, weight: Int)

object EnvironmentManager {
  private val events: Seq[RandomEvent] = Seq(
    RandomEvent(Clear, 100),
    RandomEvent(Rain, 50),
    RandomEvent(Drought, 10)
  )

  private val weights: Int = events.map(event => event.weight).sum

  private val eventsProbabilities = events.scanLeft((0, Option.empty[EnvironmentEvent])) {
    case ((acc, _), nextEvent) =>
      (acc + nextEvent.weight, Some(nextEvent.event))
  }.tail

  private val initialDelay = 5.seconds
  private val interval = 10.seconds

  private def randomEvent(): EnvironmentEvent = {
    val selectedEvent: Double = Random.nextInt(weights)

    eventsProbabilities.find {
      case (acc, _) => selectedEvent < acc
    }.get._2.get
  }

  private def handleEvent(event: EnvironmentEvent): ResourcesRequests = {
    event match {
      case Rain => EnvironmentalEventModifier.rain()
      case Drought => EnvironmentalEventModifier.drought()
      case Clear => EnvironmentalEventModifier.clearWeather()
    }
  }

  def apply(resourcesManager: ActorRef[ResourcesRequests]): Behavior[EnvironmentMessage] = {
    Behaviors.setup { context =>
      implicit val ec: ExecutionContextExecutor = context.executionContext

      val scheduler: Cancellable = context.system.scheduler.scheduleWithFixedDelay(
        initialDelay,
        interval
      )(
        () => context.self ! TimeTicker("tick")
      )

      behavior(resourcesManager, scheduler)
    }
  }

  private def behavior(resourcesManager: ActorRef[ResourcesRequests], scheduler: Cancellable): Behavior[EnvironmentMessage] =
    Behaviors.receive[EnvironmentMessage] { (context, message) =>
        message match {
          case TimeTicker(tickType) =>
            val event = randomEvent()
            resourcesManager ! handleEvent(event)

            Behaviors.same
        }
      }
      .receiveSignal {
        case (context, PostStop) =>
          scheduler.cancel()
          Behaviors.same
      }
}


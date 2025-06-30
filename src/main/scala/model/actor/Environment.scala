package com.zy.societapreistorica
package model.actor

import model.utils.EnvironmentalEventModifier

import akka.actor.{Actor, ActorRef, Cancellable}

import scala.concurrent.duration.*
import scala.util.Random

sealed trait EnvironmentEvent
case object Rain extends EnvironmentEvent
case object Drought extends EnvironmentEvent
case object Clear extends EnvironmentEvent

case class EventRandom(event: EnvironmentEvent, weight: Int)

class Environment(resourceManager: ActorRef) extends Actor {
  import context.dispatcher

  private val ticker: Option[Cancellable] = None

  private val events: Seq[EventRandom] = Seq(
    EventRandom(Clear, 100),
    EventRandom(Drought, 10),
    EventRandom(Rain, 50)
  )

  private val weights: Int = events.map(event => event.weight).sum

  private val eventsProbabilities = events.scanLeft((0, Option.empty[EnvironmentEvent])) {
    case ((acc, _), nextEvent) =>
      (acc + nextEvent.weight, Some(nextEvent.event))
  }.tail

  private def randomEvent(): EnvironmentEvent = {
    val selectedEvent: Double = Random.nextInt(weights)

    eventsProbabilities.find {
      case (acc, _) => selectedEvent < acc
    }.get._2.get
  }
  
  private def handleEvent(event: EnvironmentEvent): ResourceEvent = {
    event match {
      case Rain => EnvironmentalEventModifier.rain()
      case Drought => EnvironmentalEventModifier.drought()
      case Clear => EnvironmentalEventModifier.clearWeather()
    }
  }

  override def preStart(): Unit = {
    super.preStart()

    val ticker = Some(context.system.scheduler.scheduleWithFixedDelay(5.seconds, 10.seconds, self, "tick"))
  }

  override def postStop(): Unit = {
    super.postStop()

    ticker.foreach(
      tick => tick.cancel()
    )
  }

  override def receive: Receive = {
    case "tick" =>
      val event = randomEvent()
      resourceManager ! handleEvent(event)
  }
}

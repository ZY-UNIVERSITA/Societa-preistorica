package com.zy.societapreistorica
package model

import akka.actor.Actor

case class GetResource(inhabitantName: String, resourceType: String)

class Resources() extends Actor {
  private var water = 5
  private var wood = 5

  def receive: Receive = {
    case GetResource(inhabitantName, "water") => {
      if (water > 0) {
        water -= 1
        println(s"Water has been pulled out by ${inhabitantName}. Remaining: ${water}")
      } else {
        println(s"Not enough water: ${water}")
      }
    }
  }
}

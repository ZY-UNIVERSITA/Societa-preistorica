package com.zy.societapreistorica
package model.utils

import model.actor.{LoseResource, NothingResource, ReplenishResource}

object EnvironmentalEventModifier {
  def rain(): ReplenishResource = {
    ReplenishResource("water", 1)
  }

  def drought(): LoseResource = {
    LoseResource("water", 1)
  }

  def clearWeather(): NothingResource = {
    NothingResource()
  }
}

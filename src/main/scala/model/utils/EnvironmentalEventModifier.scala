package com.zy.societapreistorica
package model.utils

import model.traits.{LoseResource, ReplenishResource}

import com.zy.societapreistorica.model.entities.ResourcesEnum.WATER

object EnvironmentalEventModifier {
  def rain(): ReplenishResource = {
    ReplenishResource(WATER, 1)
  }

  def drought(): LoseResource = {
    LoseResource(WATER, 1)
  }

  def clearWeather(): ReplenishResource = {
    ReplenishResource(WATER, 0)
  }
}

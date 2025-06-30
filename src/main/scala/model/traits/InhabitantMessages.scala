package com.zy.societapreistorica
package model.traits

import model.entities.ResourcesEnum

sealed trait InhabitantMessages

case class ReceiveResource(resourceName: ResourcesEnum, quantity: Int) extends InhabitantMessages
case class NegativeResourceMessage(resourceName: ResourcesEnum) extends InhabitantMessages
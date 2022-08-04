package it.unibo.pps.entities

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.entity
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.common.GaussianProperty.GaussianAgeDistribution
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.House
class EntitiesTest extends AnyFunSuite with Matchers:
  private val id = 1;
  private val age = 10
  private val home = House(Point2D(7, 4), 5, 5)
  private val health = 70
  private val maxHealth = 70

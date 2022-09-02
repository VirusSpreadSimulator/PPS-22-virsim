package it.unibo.pps.entity.entity

import it.unibo.pps.control.engine.logics.movement.MovementLogicTest.{gridSide, house}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MAX_VALUES
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.structure.Structures.House
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EntitiesTest extends AnyFunSuite with Matchers:
  private val tolerance = 1e-0
  private val house = House((1, 0), 1, 2)
  private val entity100 = SimulationEntity(
    1,
    100,
    house.position,
    MAX_VALUES.MAX_HEALTH,
    position = Point2D(0, 10)
  )
  private val entity0 = SimulationEntity(
    1,
    0,
    house.position,
    MAX_VALUES.MAX_HEALTH,
    position = Point2D(0, 10)
  )
  private val entity80 = SimulationEntity(
    2,
    40,
    house.position,
    MAX_VALUES.MAX_HEALTH,
    position = Point2D(0, 10)
  )

  test("two entities with the same id are the same entity") {
    entity0 shouldBe entity100
  }

  test("two entities with different ids are different entities") {
    entity0 should not be entity80
  }

  test("an entity with age = 100 has 70 of health") {
    entity100.health shouldBe 70.0 +- tolerance
  }

  test("an entity with age = 0 has 100 of health") {
    entity0.health shouldBe 100.0 +- tolerance
  }

  test("an entity with age = 40 has 88 of health") {
    entity80.health shouldBe 88.0 +- tolerance
  }

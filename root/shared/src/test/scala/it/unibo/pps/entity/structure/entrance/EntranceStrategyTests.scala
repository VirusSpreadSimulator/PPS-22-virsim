package it.unibo.pps.entity.structure.entrance

import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.Structures.House
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.structure.entrance.Entrance.*

class EntranceStrategyTests extends AnyFunSuite with Matchers:
  private val house = House((1, 0), 1, 2)
  private val entity = SimulationEntity(0, 23, house.position, 80, position = (10, 5))

  test("Base entrance strategy should let everyone to enter") {
    BaseEntranceStrategy().canEnter(entity) shouldBe true
  }

  test("Filter based entrance strategy should respect the filter, true case") {
    val strategy = new BaseEntranceStrategy() with FilterBasedStrategy(_.age > 18)
    strategy.canEnter(entity) shouldBe true
  }

  test("Filter based entrance strategy should respect the filter, false case") {
    val strategy = new BaseEntranceStrategy with FilterBasedStrategy(_.age < 18)
    strategy.canEnter(entity) shouldBe false
  }

  test("Probability based entrance strategy with probability 1 should let everyone to enter") {
    val strategy = new BaseEntranceStrategy with ProbabilityBasedStrategy(1)
    strategy.canEnter(entity) shouldBe true
  }

  test("Probability based entrance strategy with probability 0 should let no-one to enter") {
    val strategy = new BaseEntranceStrategy with ProbabilityBasedStrategy(0)
    strategy.canEnter(entity) shouldBe false
  }

  test("We can mix different strategies") {
    val strategy = new BaseEntranceStrategy with FilterBasedStrategy(_.age > 18) with ProbabilityBasedStrategy(0)
    strategy.canEnter(entity) shouldBe false
  }

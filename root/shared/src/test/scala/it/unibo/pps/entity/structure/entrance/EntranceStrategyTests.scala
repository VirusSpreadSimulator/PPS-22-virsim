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
    class FilteredStrategy extends BaseEntranceStrategy with FilterBasedStrategy(_.age > 18)
    FilteredStrategy().canEnter(entity) shouldBe true
  }

  test("Filter based entrance strategy should respect the filter, false case") {
    class FilteredStrategy extends BaseEntranceStrategy with FilterBasedStrategy(_.age < 18)
    FilteredStrategy().canEnter(entity) shouldBe false
  }

  test("Probability based entrance strategy with probability 1 should let everyone to enter") {
    class ProbabilityStrategy extends BaseEntranceStrategy with ProbabilityBasedStrategy(1)
    ProbabilityStrategy().canEnter(entity) shouldBe true
  }

  test("Probability based entrance strategy with probability 0 should let noone to enter") {
    class ProbabilityStrategy extends BaseEntranceStrategy with ProbabilityBasedStrategy(0)
    ProbabilityStrategy().canEnter(entity) shouldBe false
  }

  test("We can mix different strategies") {
    class MixedStrategy
        extends BaseEntranceStrategy
        with FilterBasedStrategy(_.age > 18)
        with ProbabilityBasedStrategy(0)
    MixedStrategy().canEnter(entity) shouldBe false
  }

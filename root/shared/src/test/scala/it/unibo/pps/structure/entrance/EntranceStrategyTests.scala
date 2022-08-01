package it.unibo.pps.structure.entrance

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.structure.entrance.Entrance.*

//todo: fix entity type when @accursi's entity is modeled
class EntranceStrategyTests extends AnyFunSuite with Matchers:
  test("Base entrance strategy should let everyone to enter") {
    BaseEntranceStrategy().canEnter("random") shouldBe true
  }

  test("Filter based entrance strategy should respect the filter, true case") {
    class FilteredStrategy extends BaseEntranceStrategy with FilterBasedStrategy(_.contains("rand"))
    FilteredStrategy().canEnter("random") shouldBe true
  }

  test("Filter based entrance strategy should respect the filter, false case") {
    class FilteredStrategy extends BaseEntranceStrategy with FilterBasedStrategy(_.contains("x"))
    FilteredStrategy().canEnter("random") shouldBe false
  }

  test("Probability based entrance strategy with probability 1 should let everyone to enter") {
    class ProbabilityStrategy extends BaseEntranceStrategy with ProbabilityBasedStrategy(1)
    ProbabilityStrategy().canEnter("random") shouldBe true
  }

  test("Probability based entrance strategy with probability 0 should let noone to enter") {
    class ProbabilityStrategy extends BaseEntranceStrategy with ProbabilityBasedStrategy(0)
    ProbabilityStrategy().canEnter("random") shouldBe false
  }

  test("We can mix different strategies") {
    class MixedStrategy
        extends BaseEntranceStrategy
        with FilterBasedStrategy(_.contains("rand"))
        with ProbabilityBasedStrategy(0)
    MixedStrategy().canEnter("random") shouldBe false
  }

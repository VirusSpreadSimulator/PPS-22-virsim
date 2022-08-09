package it.unibo.pps.common

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.common.ProblableEvents.*
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given

class ProbableTests extends AnyFunSuite with Matchers:
  test("A probable event has a probability") {
    1.probability shouldEqual 1
  }

  test("A 100% probable event should happen") {
    1.isHappening shouldBe ProbabilityResult.HAPPENED
  }

  test("A 0% probable event should not happen") {
    0.isHappening shouldBe ProbabilityResult.NOTHAPPENED
  }

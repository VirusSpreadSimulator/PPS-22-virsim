package it.unibo.pps.entities

import it.unibo.pps.entity.entity.HealthCalculator
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class HealthCalculatorTest extends AnyFunSuite with Matchers:
  test("an entity with 100 year should have 70 of health") {
    HealthCalculator.calculateHealth(100)
  }

  test("an entity with 0 year should have 100 of health") {
    HealthCalculator.calculateHealth(100)
  }

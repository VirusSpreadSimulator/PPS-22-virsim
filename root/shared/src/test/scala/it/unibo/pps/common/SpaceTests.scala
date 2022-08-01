package it.unibo.pps.common

import it.unibo.pps.entity.common.Space.Point2D
import org.scalactic.Equality
import org.scalactic.TolerantNumerics
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpaceTests extends AnyFunSuite with Matchers:
  private val p1 = Point2D(10, 20)
  private val p2 = (5L, 4L)
  private val tolerance = 1e-6

  test("The combine operator works on both the coordinates") {
    p1.combine(p2)(_ / _) shouldBe Point2D(2, 5)
  }

  test("The + operator on two Point makes the sum of the coordinates") {
    p1 + p2 shouldBe Point2D(15, 24)
  }

  test("The - operator on two Point makes the subtraction of the coordinates") {
    p1 - p2 shouldBe Point2D(5, 16)
  }

  test("The * operator on two Point makes the product of the coordinates") {
    p1 * p2 shouldBe Point2D(50, 80)
  }

  test("It's possible to compute the distance between two points") {
    p1 distanceTo p2 shouldBe (16.763055 +- tolerance)
  }

package it.unibo.pps.common

import it.unibo.pps.entity.common.Time.DurationTime
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpaceTimeTests extends AnyFunSuite with Matchers:
  test("It's possible to obtain length and unit from a duration") {
    import scala.concurrent.duration.MILLISECONDS
    val duration = DurationTime(100, MILLISECONDS)
    duration.length shouldBe 100
    duration.unit shouldBe MILLISECONDS
  }

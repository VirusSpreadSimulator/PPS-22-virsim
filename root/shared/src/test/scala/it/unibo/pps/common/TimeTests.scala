package it.unibo.pps.common

import it.unibo.pps.entity.common.Time.DurationTime
import it.unibo.pps.entity.common.Time.TimeStamp
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TimeTests extends AnyFunSuite with Matchers:
  private val time = 100

  test("It's possible to obtain length and unit from a duration") {
    import scala.concurrent.duration.MILLISECONDS
    val duration = DurationTime(time, MILLISECONDS)
    duration.length shouldBe time
    duration.unit shouldBe MILLISECONDS
  }

  test("A timestamp should be convertible to its absolute value respect the start of the simulation") {
    val iterationNumber = 2
    val timeStamp = TimeStamp(time, iterationNumber)
    timeStamp.toAbsoluteValue() shouldBe (time * iterationNumber)
  }

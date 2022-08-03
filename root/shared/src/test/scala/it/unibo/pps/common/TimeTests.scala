package it.unibo.pps.common

import it.unibo.pps.entity.common.Time.DurationTime
import it.unibo.pps.entity.common.Time.TimeStamp
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TimeTests extends AnyFunSuite with Matchers:
  private val time = 100
  private val iterationNumber = 2
  private val timeStamp = TimeStamp(time, iterationNumber)

  test("It's possible to obtain length and unit from a duration") {
    import scala.concurrent.duration.MILLISECONDS
    val duration = DurationTime(time, MILLISECONDS)
    duration.length shouldBe time
    duration.unit shouldBe MILLISECONDS
  }

  test("A timestamp tick value should be convertible to its absolute value respect the start of the simulation") {
    timeStamp.toAbsoluteTickValue() shouldBe (time * iterationNumber)
  }

  test("A timestamp tick value should be convertible to minutes") {
    timeStamp.toMinutes() shouldBe time
  }

  test("A timestamp tick value should be convertible to absolute minutes since the start of the simulation") {
    timeStamp.toAbsoluteMinutes() shouldBe (iterationNumber * timeStamp.toMinutes())
  }

  test("Two timestamp should be comparable in order to understand which one is the latest") {
    val lowerTimestamp = TimeStamp(time, iterationNumber - 1)
    timeStamp > lowerTimestamp shouldBe true
  }

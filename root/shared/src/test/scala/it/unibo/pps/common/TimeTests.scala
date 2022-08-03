package it.unibo.pps.common

import it.unibo.pps.entity.common.Time.DurationTime
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.common.Time.TimeConfiguration
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TimeTests extends AnyFunSuite with Matchers:
  private val absoluteTime = 1540
  private val time = 100
  private val iterationNumber = 1
  private val timeStamp = TimeStamp(absoluteTime)

  test("It's possible to obtain length and unit from a duration") {
    import scala.concurrent.duration.MILLISECONDS
    val duration = DurationTime(time, MILLISECONDS)
    duration.length shouldBe time
    duration.unit shouldBe MILLISECONDS
  }

  test("Timestamp tick number should be convertible to its relative value respect to the iteration start") {
    timeStamp.ticksSinceIterationStart shouldBe time
  }

  test("A timestamp tick value should be convertible to minutes") {
    timeStamp.toMinutes() shouldBe (time / TimeConfiguration.TICKS_PER_MINUTE)
  }

  test("A timestamp tick value should be convertible to absolute minutes since the start of the simulation") {
    timeStamp.toAbsoluteMinutes() shouldBe (absoluteTime / TimeConfiguration.TICKS_PER_MINUTE)
  }

  test("We can get the current iteration number from a timestamp") {
    timeStamp.iteration shouldBe iterationNumber
  }

  test("Two timestamp should be comparable in order to understand which one is the latest") {
    val lowerTimestamp = TimeStamp(absoluteTime - 1)
    timeStamp > lowerTimestamp shouldBe true
  }

package it.unibo.pps.common

import it.unibo.pps.entity.common.Time.DurationTime
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.common.Time.TimeConfiguration
import it.unibo.pps.entity.common.Time.Period
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scala.concurrent.duration.MINUTES

class TimeTests extends AnyFunSuite with Matchers:
  private val time = 100
  private val iterationNumber = 1
  private val timeStamp = TimeStamp(time, iterationNumber)

  test("It's possible to obtain length and unit from a duration") {
    val duration = DurationTime(time, MINUTES)
    duration.length shouldBe time
    duration.unit shouldBe MINUTES
  }

  test("Timestamp tick number should be convertible to its relative value respect to the iteration start") {
    timeStamp.relativeTicks shouldBe time
  }

  test("A timestamp tick value should be convertible to minutes") {
    timeStamp.toMinutes shouldBe (time / TimeConfiguration.TICKS_PER_MINUTE)
  }

  test("We can get the current iteration number from a timestamp") {
    timeStamp.iteration shouldBe iterationNumber
  }

  test("If the client insert a relative number higher than the maximum tick for iteration it will be handled") {
    val relativeTime = 100
    val iteration = 1
    val time = iteration * TimeConfiguration.TICKS_PER_DAY + relativeTime
    TimeStamp(time) shouldEqual TimeStamp(relativeTime, iteration)
  }

  test("Two timestamp should be comparable in order to understand which one is the latest, same iteration, lower") {
    val lowerTimestamp = TimeStamp(time - 1, iterationNumber)
    timeStamp > lowerTimestamp shouldBe true
  }

  test("Two timestamp should be comparable in order to understand which one is the latest, same iteration, higher") {
    val higherTimestamp = TimeStamp(time + 1, iterationNumber)
    timeStamp < higherTimestamp shouldBe true
  }

  test(
    "Two timestamp should be comparable in order to understand which one is the latest, different iteration, lower"
  ) {
    val lowerTimestamp = TimeStamp(time, iterationNumber - 1)
    timeStamp > lowerTimestamp shouldBe true
  }

  test(
    "Two timestamp should be comparable in order to understand which one is the latest, different iteration, higher"
  ) {
    val higherTimestamp = TimeStamp(time, iterationNumber + 1)
    timeStamp < higherTimestamp shouldBe true
  }

  test(
    "Two timestamp should be comparable in order to understand which one is the latest, equal"
  ) {
    val higherTimestamp = TimeStamp(time, iterationNumber)
    timeStamp == higherTimestamp shouldBe true
  }

  test(
    "It is possible to add a DurationTime to a timestamp to obtain an incremented timestamp, no overflow relative ticks"
  ) {
    val minutesToAdd = 10
    val durationTime = DurationTime(minutesToAdd, MINUTES)
    timeStamp + durationTime shouldEqual TimeStamp(
      time + minutesToAdd * TimeConfiguration.TICKS_PER_MINUTE,
      iterationNumber
    )
  }

  test(
    "It is possible to add a DurationTime to a timestamp to obtain an incremented timestamp, overflow relative ticks"
  ) {
    val minutesToAdd = 2000
    val durationTime = DurationTime(minutesToAdd, MINUTES)
    timeStamp + durationTime shouldEqual TimeStamp(
      (time + minutesToAdd * TimeConfiguration.TICKS_PER_MINUTE) % TimeConfiguration.TICKS_PER_DAY,
      iterationNumber + 1
    )
  }

  test("It is possible to add a tick to a timestamp to obtain an incremented timestamp, no overflow relative ticks") {
    val tickToAdd = 1
    timeStamp + tickToAdd shouldEqual TimeStamp(time + tickToAdd, iterationNumber)
  }

  test("It is possible to add a tick to a timestamp to obtain an incremented timestamp, overflow relative ticks") {
    val tickToAdd = 100
    val iterationToAdd = 1
    val absoluteTicks = tickToAdd + TimeConfiguration.TICKS_PER_DAY * iterationToAdd
    timeStamp + absoluteTicks shouldEqual TimeStamp(time + tickToAdd, iterationNumber + iterationToAdd)
  }

  test("We can obtain the period of the day from the time, day") {
    TimeStamp(TimeConfiguration.DAY_MINUTES_UPPER_BOUND - 10).period shouldBe Period.DAY
  }

  test("We can obtain the period of the day from the time, night") {
    TimeStamp(TimeConfiguration.DAY_MINUTES_UPPER_BOUND + 10).period shouldBe Period.NIGHT
  }

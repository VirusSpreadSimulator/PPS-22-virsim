package it.unibo.pps.structure.entrance

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence
import it.unibo.pps.entity.structure.entrance.Permanence.PermanenceStatus

import scala.concurrent.duration.MINUTES

class PermanenceTests extends AnyFunSuite with Matchers:
  private val entity = "entity" // todo: refactor when entity is defined
  private val timestamp = TimeStamp(100)
  private val permanenceDuration = DurationTime(10, MINUTES)
  private val permanenceValid = DurationTime(1, MINUTES)
  private val permanenceOver = DurationTime(11, MINUTES)
  private val permanence = EntityPermanence(entity, timestamp, permanenceDuration)

  test("Permanence should be able to understand when its over, valid case") {
    val timeStampValid = timestamp + permanenceValid
    permanence.status(timeStampValid) shouldBe PermanenceStatus.VALID
  }

  test("Permanence should be able to understand when its over, over case") {
    val timeStampOver = timestamp + permanenceOver
    permanence.status(timeStampOver) shouldBe PermanenceStatus.OVER
  }
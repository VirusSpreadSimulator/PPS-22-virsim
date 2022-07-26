package it.unibo.pps.entity.structure.entrance

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence
import it.unibo.pps.entity.structure.entrance.Permanence.PermanenceStatus
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.Structures.House
import scala.concurrent.duration.MINUTES

class PermanenceTests extends AnyFunSuite with Matchers:
  private val house = House((1, 0), 1, 2)
  private val entity = SimulationEntity(0, 23, house.position, 80, position = (10, 5))
  private val timestamp = TimeStamp(100)
  private val permanenceDuration = DurationTime(10, MINUTES)
  private val permanenceValid = DurationTime(1, MINUTES)
  private val permanenceOver = DurationTime(11, MINUTES)
  private val permanence = EntityPermanence(entity, timestamp, permanenceDuration)

  test("It is possible to obtain the entity of the permanence") {
    permanence.entity shouldBe entity
  }

  test("It is possible to obtain the timestamp of the permanence") {
    permanence.timestamp shouldBe timestamp
  }

  test("It is possible to obtain the duration of the permanence") {
    permanence.permanenceDuration shouldBe permanenceDuration
  }

  test("Permanence should be able to understand when its over, valid case") {
    val timeStampValid = timestamp + permanenceValid
    permanence.status(timeStampValid) shouldBe PermanenceStatus.VALID
  }

  test("Permanence should be able to understand when its over, over case") {
    val timeStampOver = timestamp + permanenceOver
    permanence.status(timeStampOver) shouldBe PermanenceStatus.OVER
  }

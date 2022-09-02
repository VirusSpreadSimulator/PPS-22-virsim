package it.unibo.pps.entity
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MAX_VALUES
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.Structures.House
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EnvironmentTest extends AnyFunSuite with Matchers:
  private val env = Samples.sampleEnv
  private val house = House((1, 0), 1, 2)
  private val entity = SimulationEntity(
    1,
    100,
    house.position,
    MAX_VALUES.MAX_HEALTH,
    position = Point2D(0, 10)
  )
  test("call to update without parameters does not modify the env") {
    env.update() shouldBe env
  }

  test("call to update with entities as externalEntities parameter, modify the set of external entities") {
    val newExternalEntities = Set(entity)
    env.update(externalEntities = newExternalEntities).externalEntities shouldBe newExternalEntities
  }

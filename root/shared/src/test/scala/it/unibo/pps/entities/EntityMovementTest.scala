package it.unibo.pps.entities

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.engine.behaviouralLogics.EntityLogic
import it.unibo.pps.control.engine.behaviouralLogics.CalculateNextMovement
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.environment.EnvironmentModule.Component
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization.TreatmentQuality
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Component
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}

import scala.util.Random
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EntityMovementTest extends AnyFunSuite with Matchers:
  val nEntities = 16
  val ageDistribution: GaussianIntDistribution = GaussianIntDistribution(27, 10)
  val gridSize = 30
  val peoplePerHouse = 4
  val house: House = House((Random.nextInt(gridSize), gridSize - 1), 0.5, peoplePerHouse)
  val entity: BaseEntity = BaseEntity(
    1,
    Math.max(ageDistribution.next(), 1),
    house,
    position = Point2D(Random.nextInt(gridSize), Random.nextInt(gridSize)),
    immunity = if Random.nextBoolean() then 10 else 0
  )
  object FakeEnv extends EnvironmentModule.Interface:
    override val env: EnvironmentModule.Environment = Environment.empty
  val initializedEnv: EnvironmentModule.Environment =
    FakeEnv.env.initialized(gridSize, Set(entity), Virus(), Set())

  test("an entity can move") {
    val movementLogic: EntityLogic = CalculateNextMovement()
    val updatedEnv = movementLogic.execute(initializedEnv)
    updatedEnv.entities.toList.head.asInstanceOf[BaseEntity].position should not equal entity.position
  }

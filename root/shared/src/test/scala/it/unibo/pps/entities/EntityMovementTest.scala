package it.unibo.pps.entities

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.engine.logics.entrance.EntranceLogic
import it.unibo.pps.control.engine.logics.entrance.EntranceLogicTest.{
  buildings,
  enterIntoStructureLogic,
  entitiesNear,
  firstEnv,
  numberOfExternalEntities
}
import it.unibo.pps.control.engine.logics.movement.MovementLogic
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.{Component, Environment}
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization.TreatmentQuality
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Moving
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Component
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import weaver.monixcompat.SimpleTaskSuite

import scala.util.Random
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

object EntityMovementTest extends SimpleTaskSuite:
  private val house = House((1, 0), 1, 2)
  private val entities: Seq[SimulationEntity with Moving] = Seq(
    SimulationEntity(0, 20, house.position, 80, position = Point2D(0, 20))
  )
  private val buildings: Set[SimulationStructure] = Set(
    GenericBuilding(
      Point2D(0, 21),
      0.5,
      4
    )
  )

  def env: Environment =
    object InfectedEnv extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(externalEntities = entities.toSet, structures = buildings)
    InfectedEnv.env

  val baseEnv: Environment = env

  val movementLogic: MovementLogic = MovementLogic()

  test("an entity can move") {
    for updatedEnv <- movementLogic(baseEnv)
    yield expect(!(updatedEnv eq baseEnv))
  }

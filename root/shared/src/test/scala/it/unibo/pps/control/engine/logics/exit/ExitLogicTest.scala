package it.unibo.pps.control.engine.logics.exit

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, House, SimulationStructure}
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence
import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import weaver.monixcompat.SimpleTaskSuite
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, MINUTES}

object ExitLogicTest extends SimpleTaskSuite:
  private val house = House((1, 0), 1, 2)
  private val buildingPosition = Point2D(0, 21)
  private val exitFromStructureLogic: ExitLogic = ExitLogic()
  private val entitiesInsideStructure: Set[EntityPermanence] = Set(
    EntityPermanence(
      SimulationEntity(0, 20, house.position, 80, position = Point2D(0, 21), movementGoal = MovementGoal.NO_MOVEMENT),
      TimeStamp(),
      FiniteDuration(0, MILLISECONDS)
    ),
    EntityPermanence(
      SimulationEntity(1, 20, house.position, 80, position = Point2D(0, 21), movementGoal = MovementGoal.NO_MOVEMENT),
      TimeStamp(),
      FiniteDuration(20, MINUTES)
    )
  )
  private val buildings: Set[SimulationStructure] = Set(
    GenericBuilding(
      buildingPosition,
      0.5,
      4,
      entities = entitiesInsideStructure
    )
  )

  def env: Environment =
    object Env extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(time = TimeStamp(10), externalEntities = Set(), structures = buildings)
    Env.env

  test("an entity with status OVER has to exit from the structure") {
    for updatedEnv <- exitFromStructureLogic(env)
    yield expect(numberOfExternalEntities(updatedEnv) != numberOfExternalEntities(env))
  }

  test("the exit logic doesn't create other entities") {
    for updatedEnv <- exitFromStructureLogic(env)
    yield expect(numberOfAllEntities(updatedEnv) == numberOfAllEntities(env))
  }

  test("only an entity of the environment can exit") {
    for updatedEnv <- exitFromStructureLogic(env)
    yield expect(numberOfExternalEntities(updatedEnv) == 1)
  }

  test("exited entities has RANDOM_MOVEMENT as MovementGoal") {
    for updatedEnv <- exitFromStructureLogic(env)
    yield expect(updatedEnv.externalEntities.forall(_.movementGoal == MovementGoal.RANDOM_MOVEMENT))
  }

  test("the position of exited entities is different from the position of the structure") {
    for updatedEnv <- exitFromStructureLogic(env)
    yield expect(updatedEnv.externalEntities.forall(_.position != buildingPosition))
  }

  private def numberOfExternalEntities(env: Environment): Int =
    env.externalEntities.size

  private def numberOfAllEntities(env: Environment): Int =
    env.allEntities.size

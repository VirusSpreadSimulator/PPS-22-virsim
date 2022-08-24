package it.unibo.pps.control.engine.logics.movement

import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, House, SimulationStructure}
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence
import weaver.monixcompat.SimpleTaskSuite
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, MINUTES}

object MovementLogicTest extends SimpleTaskSuite:
  private val house = House((1, 0), 1, 2)
  private val gridSide = 50
  private val entityNoMovement = SimulationEntity(
    5,
    20,
    house.position,
    80,
    position = Point2D(gridSide / 2, gridSide / 2),
    movementGoal = MovementGoal.NO_MOVEMENT
  )
  private val entityGoHome = SimulationEntity(
    6,
    20,
    house.position,
    80,
    position = Point2D(gridSide / 2, gridSide / 2),
    movementGoal = MovementGoal.BACK_TO_HOME
  )
  private val entities: Set[SimulationEntity] = Set(
    SimulationEntity(
      0,
      20,
      house.position,
      80,
      position = Point2D(0, gridSide),
      movementGoal = MovementGoal.RANDOM_MOVEMENT
    ),
    SimulationEntity(
      1,
      20,
      house.position,
      80,
      position = Point2D(gridSide, gridSide),
      movementGoal = MovementGoal.RANDOM_MOVEMENT
    ),
    SimulationEntity(
      2,
      20,
      house.position,
      80,
      position = Point2D(gridSide, 0),
      movementGoal = MovementGoal.RANDOM_MOVEMENT
    ),
    SimulationEntity(3, 20, house.position, 80, position = Point2D(0, 0), movementGoal = MovementGoal.RANDOM_MOVEMENT),
    SimulationEntity(
      4,
      20,
      house.position,
      80,
      position = Point2D(gridSide / 2, gridSide / 2),
      movementGoal = MovementGoal.RANDOM_MOVEMENT
    ),
    entityNoMovement,
    entityGoHome
  )

  def env: Environment =
    object Env extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(externalEntities = entities, gridSide = gridSide)
    Env.env

  val movementLogic: MovementLogic = MovementLogic()

  test("the exit logic doesn't create other entities") {
    for updatedEnv <- movementLogic(env)
    yield expect(numberOfAllEntities(updatedEnv) == numberOfAllEntities(env))
  }

  test("after a movement all entities remains in the environment") {
    for updatedEnv <- movementLogic(env)
    yield expect(
      updatedEnv.externalEntities.forall(e =>
        e.position.x >= 0 && e.position.x <= gridSide && e.position.y >= 0 && e.position.y <= gridSide
      )
    )
  }

  test("an entity with movement goal = NO_MOVEMENT doesn't move") {
    for updatedEnv <- movementLogic(env)
    yield expect(
      updatedEnv.externalEntities
        .find(_.movementGoal == MovementGoal.NO_MOVEMENT)
        .get
        .position == entityNoMovement.position
    )
  }

  test("an entity with movement goal = BACK_TO_HOME moves in direction of his home") {
    for updatedEnv <- movementLogic(env)
    yield expect(
      updatedEnv.externalEntities
        .find(_.movementGoal == MovementGoal.BACK_TO_HOME)
        .get
        .position
        .distanceTo(entityGoHome.homePosition) < entityGoHome.position.distanceTo(entityGoHome.homePosition)
    )
  }

  private def numberOfAllEntities(env: Environment): Int =
    env.allEntities.size

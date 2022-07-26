package it.unibo.pps.control.engine.logics.exit

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Visible
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.structure.entrance.Permanence.PermanenceStatus
import monix.eval.Task
import monocle.syntax.all.*

import scala.util.Random

/* class that contains the logics for the entry of an entity in a structure*/
class ExitLogic extends UpdateLogic:
  import it.unibo.pps.entity.common.Utils.*
  type VisibleStructure = SimulationStructure with Visible

  override def apply(environment: Environment): Task[Environment] =
    for
      updateEnv <- Task {
        environment.structures
          .foldLeft(environment)((env, structure) => checkExit(env, structure))
      }
    yield updateEnv

  /*return the updated environment after check if entities in the structure must exit*/
  private def checkExit(environment: Environment, structure: SimulationStructure): Environment =
    val entities = structure.entities
    val exitedEntities =
      entities
        .filter(e => e.status(environment.time) == PermanenceStatus.OVER)
        .map(
          _.entity
            .focus(_.movementGoal)
            .replace(MovementGoal.RANDOM_MOVEMENT)
            .focus(_.position)
            .modify(getNewEntityPosition(environment, structure, _))
        )
    val updatedStructure = exitedEntities.foldLeft(structure)((struct, entity) => struct.entityExit(entity))
    environment.update(
      externalEntities = environment.externalEntities ++ exitedEntities,
      structures = environment.structures - structure + updatedStructure
    )

  /*return the new entity position after she exit from the structure*/
  private def getNewEntityPosition(
      environment: Environment,
      structure: SimulationStructure,
      currentPosition: Point2D
  ): Point2D =
    def getAllPossiblePosition(width: Int, height: Int, position: Point2D, visibilityDistance: Double): Set[Point2D] =
      (for
        x <- List(
          -visibilityDistance.toInt - SimulationDefaults.StructuresDefault.DEFAULT_ENTITY_EXIT_DISTANCE,
          0,
          visibilityDistance.toInt + SimulationDefaults.StructuresDefault.DEFAULT_ENTITY_EXIT_DISTANCE
        )
        y <- List(
          -visibilityDistance.toInt - SimulationDefaults.StructuresDefault.DEFAULT_ENTITY_EXIT_DISTANCE,
          0,
          visibilityDistance.toInt + SimulationDefaults.StructuresDefault.DEFAULT_ENTITY_EXIT_DISTANCE
        )
      yield position + Point2D(x, y))
        .filter(point =>
          point.x >= 0 && point.y >= 0 && point.x < width && point.y <= height && (point.x != position.x || point.y != position.y)
        )
        .toSet
    Random
      .shuffle(
        getAllPossiblePosition(
          environment.gridSide,
          environment.gridSide,
          currentPosition,
          structure.asInstanceOf[VisibleStructure].visibilityDistance
        )
      )
      .head

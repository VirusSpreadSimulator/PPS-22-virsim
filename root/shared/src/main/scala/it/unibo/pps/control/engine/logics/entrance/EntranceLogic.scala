package it.unibo.pps.control.engine.logics.entrance

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Visible
import it.unibo.pps.entity.structure.Structures.{House, SimulationStructure}
import monix.eval.Task
import monocle.syntax.all.*

class EntranceLogic extends UpdateLogic:
  import it.unibo.pps.entity.common.Utils.*
  type VisibleStructure = SimulationStructure with Visible

  override def apply(environment: Environment): Task[Environment] =
    for
      updateEnv <- Task {
        environment.externalEntities
          .foldLeft(environment)((env, entity) => tryToEnter(env, entity).getOrElse(env))

      }
    yield updateEnv

  private def tryToEnter(environment: Environment, entity: SimulationEntity): Option[Environment] =
    for
      oldStruct <- environment.structures
        .select[VisibleStructure]
        .find(structure =>
          (entity.movementGoal != MovementGoal.BACK_TO_HOME && entity.position.distanceTo(
            structure.position
          ) <= structure.visibilityDistance) || (entity.movementGoal == MovementGoal.BACK_TO_HOME && entity.homePosition == structure.position && entity.position
            .distanceTo(structure.position) <= structure.visibilityDistance)
        )
      updatedStruct = oldStruct.tryToEnter(entity, environment.time)
      if updatedStruct.entities.map(_.entity).contains(entity)
    yield environment.update(
      externalEntities = environment.externalEntities - entity,
      structures = environment.structures - oldStruct + updatedStruct.updateEntitiesInside(entity =>
        Some(
          entity
            .focus(_.movementGoal)
            .replace(MovementGoal.NO_MOVEMENT)
            .focus(_.position)
            .replace(updatedStruct.position)
        )
      )
    )

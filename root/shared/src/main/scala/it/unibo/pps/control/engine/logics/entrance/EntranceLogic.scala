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
    yield {
      println("size = " + updateEnv.externalEntities.size)
      updateEnv
    }

  private def tryToEnter(environment: Environment, entity: SimulationEntity): Option[Environment] =
    val oldStruct = environment.structures
      .select[VisibleStructure]
      .find(structure => entity.position.distanceTo(structure.position) <= structure.visibilityDistance)
    if oldStruct.isDefined then
      val updatedStruct = oldStruct.get.tryToEnter(entity, environment.time)
      if updatedStruct.entities.map(_.entity).contains(entity) then
        if updatedStruct.isInstanceOf[House] then
          println("entity entered in home = " + updatedStruct + " is " + entity.id)
        Some(
          environment.update(
            externalEntities = environment.externalEntities - entity,
            structures = environment.structures - oldStruct.get + updatedStruct.updateEntitiesInside(entity =>
              Some(
                entity
                  .focus(_.movementGoal)
                  .replace(MovementGoal.NO_MOVEMENT)
                  .focus(_.position)
                  .replace(updatedStruct.position)
              )
            )
          )
        )
      else None
    else None

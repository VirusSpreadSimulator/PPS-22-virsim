package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic

import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import monix.eval.Task
import it.unibo.pps.control.loader.configuration.SimulationDefaults.*
import it.unibo.pps.entity.entity.Infection.Severity
import monocle.syntax.all.*

object EntityStateLogic:
  /** Logic to update the state of all the entities inside the environment */
  class UpdateEntityStateLogic extends UpdateLogic:
    import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.Updates.*
    override def apply(env: Environment): Task[Environment] =
      for
        environmentExternalUpdated <- updateExternalEntitiesHealth(env)
        environmentExtIntUpdated <- updateInternalEntitiesHealth(environmentExternalUpdated)
      yield environmentExtIntUpdated

  private object Updates:
    def updateInternalEntitiesHealth(env: Environment): Task[Environment] =
      for
        structures <- Task(env.structures)
        updatedStructures <- Task {
          for struct <- structures
          yield struct.updateEntitiesInside(entity =>
            Some(handleSingleEntity(entity, env)).filter(_.health > MIN_VALUES.MIN_HEALTH)
          )
        }
        deadEntities <- Task {
          structures
            .flatMap(_.entities)
            .map(_.entity)
            .filter(entity => !updatedStructures.flatMap(_.entities).map(_.entity.id).contains(entity.id))
        }
      yield env.update(structures = updatedStructures, deadEntities = env.deadEntities ++ deadEntities)

    def updateExternalEntitiesHealth(env: Environment): Task[Environment] =
      for
        entities <- Task(env.externalEntities)
        updatedEntities <- Task(entities.map(handleSingleEntity(_, env)))
        deadEntities <- Task(updatedEntities.filter(_.health <= MIN_VALUES.MIN_HEALTH))
      yield env.update(
        externalEntities = updatedEntities -- deadEntities,
        deadEntities = env.deadEntities ++ deadEntities
      )

    def handleSingleEntity(entity: SimulationEntity, env: Environment): SimulationEntity = entity.infection match
      case Some(infection) =>
        entity
          .focus(_.health)
          .modify(_ - VirusDefaults.HEALTH_INFECTED_LOSS * getSeverityValue(infection.severity))
          .focus(_.infection)
          .modify(infection => infection.filter(inf => inf.timeOfTheInfection + inf.duration > env.time))
          .andIf(_.infection.isEmpty)(
            _.focus(_.immunity).modify(i => Math.min(MAX_VALUES.MAX_IMMUNITY, i + VirusDefaults.IMMUNITY_GAIN_RECOVERY))
          )
      case None =>
        entity
          .focus(_.health)
          .modify(value => Math.min(value + VirusDefaults.HEALTH_GAIN, entity.maxHealth))
          .focus(_.immunity)
          .modify(i => Math.max(MIN_VALUES.MIN_IMMUNITY, i - VirusDefaults.IMMUNITY_LOSS))

    // todo: to be deleted after Severity modification
    def getSeverityValue(s: Severity): Double = s match {
      case Severity.SERIOUS(value) => value;
      case Severity.LIGHT(value) => value
    }

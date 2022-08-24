package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.*
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import monocle.syntax.all.*

/** Module that contains the entity state logic concepts. */
object EntityStateLogic:
  /** Logic to update the state of all the entities inside the environment. */
  class UpdateEntityStateLogic extends UpdateLogic:
    import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.Updates.*
    override def apply(env: Environment): Task[Environment] =
      for
        environmentExternalUpdated <- updateExternalEntitiesHealth(env)
        environmentExtIntUpdated <- updateInternalEntitiesHealth(environmentExternalUpdated)
      yield environmentExtIntUpdated

  private object Updates:
    /** Update the state of entities that are inside the structures. */
    def updateInternalEntitiesHealth(env: Environment): Task[Environment] =
      for
        structures <- Task(env.structures)
        updatedHealthStructures <- Task { // no check on dead
          for struct <- structures
          yield struct.updateEntitiesInside(entity => Some(handleSingleEntity(entity, env)))
        }
        updatedStructures <- Task { // check on dead
          for struct <- updatedHealthStructures
          yield struct.updateEntitiesInside(Some(_).filter(_.health > MIN_VALUES.MIN_HEALTH))
        }
        deadEntities <- Task { // now it's possible to understand who is dead in the update obtaining the updated entity
          updatedHealthStructures
            .flatMap(_.entities.map(_.entity))
            .diff(updatedStructures.flatMap(_.entities.map(_.entity)))
        }
      yield env.update(structures = updatedStructures, deadEntities = env.deadEntities ++ deadEntities)

    /** Update the state of external entities. */
    def updateExternalEntitiesHealth(env: Environment): Task[Environment] =
      for
        entities <- Task(env.externalEntities)
        updatedEntities <- Task(entities.map(handleSingleEntity(_, env)))
        deadEntities <- Task(updatedEntities.filter(_.health <= MIN_VALUES.MIN_HEALTH))
      yield env.update(
        externalEntities = updatedEntities -- deadEntities,
        deadEntities = env.deadEntities ++ deadEntities
      )

    /** Method to handle the single entity not depending on where it is. */
    def handleSingleEntity(entity: SimulationEntity, env: Environment): SimulationEntity = entity.infection match
      case Some(infection) =>
        entity
          .focus(_.health)
          .modify(h =>
            Math.max(MIN_VALUES.MIN_HEALTH, h - VirusDefaults.HEALTH_INFECTED_LOSS * infection.severity.value)
          )
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

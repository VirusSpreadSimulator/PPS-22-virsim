package it.unibo.pps.control.engine.logics.infection

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.VirusDefaults
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.*
import monix.eval.Task
import monocle.syntax.all.*

/** Module that contains the entity infection logic concepts. */
object InfectionLogic:
  /** It models a probable infection of an entity in the environment, outside to structures. */
  case class ExternalProbableInfection(env: Environment, entity: SimulationEntity, infectors: Set[SimulationEntity])
  /** It models a probable infection of an entity inside a structure. */
  case class InternalProbableInfection(env: Environment, entity: SimulationEntity, structure: SimulationStructure)

  /** Extension methods useful during the computations. */
  extension (e: SimulationEntity)
    /** It allow to create and set the infection on an entity. */
    def infected(timestamp: TimeStamp, virus: Virus): SimulationEntity =
      import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
      import it.unibo.pps.entity.entity.Infection.Severity
      import it.unibo.pps.entity.entity.Infection
      import scala.concurrent.duration.DAYS
      val severity = if virus.severeDeseaseProbability.isHappening then Severity.SERIOUS() else Severity.LIGHT()
      val durationDistribution = GaussianDurationTime(virus.averagePositivityDays, virus.stdDevPositivityDays, DAYS)
      e.focus(_.infection).replace(Some(Infection(severity, timestamp, durationDistribution.next())))
    /** It allow to obtain the reduction on the spread of the virus based on the current state of the mask. */
    def maskReduction: Int = if e.hasMask then VirusDefaults.MASK_REDUCER else 1

  /** Logic that handle the infection in the environment, external to structures. */
  class ExternalInfectionLogic extends UpdateLogic:
    override def apply(env: Environment): Task[Environment] =
      for
        entities <- Task(env.externalEntities)
        infected <- Task.sequence {
          entities
            .filter(_.infection.isEmpty)
            .map(e =>
              ExternalProbableInfection(
                env,
                e,
                entities.filter(other =>
                  other.infection.isDefined && e.position.distanceTo(other.position) <= env.virus.maxInfectionDistance
                )
              )
            )
            .filter(_.isHappening)
            .map(i => Task(i.entity.infected(env.time, env.virus)))
        }
      yield env.update(externalEntities = entities.filter(e => !infected.map(_.id).contains(e.id)) ++ infected.toSet)

  /** Logic that handle the infection inside the structures of the environment. */
  class InternalInfectionLogic extends UpdateLogic:
    override def apply(env: Environment): Task[Environment] =
      for
        structures <- Task(env.structures)
        infectedStructures <- Task(
          structures.filter(_.entities.map(_.entity).exists(_.infection.isDefined))
        )
        updatedStructures <- Task {
          for infectedStructure <- infectedStructures
          yield infectedStructure.updateEntitiesInside { entity =>
            Some(entity)
              .filter(_.infection.isEmpty)
              .map(e => InternalProbableInfection(env, e, infectedStructure))
              .filter(_.isHappening)
              .map(_.entity.infected(env.time, env.virus))
              .orElse(Some(entity))
          }
        }
      yield env.update(structures = structures -- infectedStructures ++ updatedStructures)

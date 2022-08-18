package it.unibo.pps.control.engine.logics.infection

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence
import monix.eval.Task

object InfectionLogic:
  /** The reducer on the probability due to the mask */
  val MASK_REDUCER: Int = 2
  type InfectingEntity = SimulationEntity with Moving with Infectious

  case class ExternalProbableInfection(env: Environment, entity: InfectingEntity, infectors: Set[InfectingEntity])
  case class InternalProbableInfection(env: Environment, entity: InfectingEntity, structure: SimulationStructure)

  extension (e: InfectingEntity)
    def infected(timestamp: TimeStamp, virus: Virus): InfectingEntity =
      import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
      import it.unibo.pps.entity.entity.Infection.Severity
      import it.unibo.pps.entity.entity.Infection
      import scala.concurrent.duration.DAYS
      val severity = if virus.severeDeseaseProbability.isHappening then Severity.SERIOUS() else Severity.LIGHT()
      val durationDistribution = GaussianDurationTime(virus.averagePositivityDays, virus.stdDevPositivityDays, DAYS)
      BaseEntity(
        e.id,
        e.age,
        e.home,
        e.immunity,
        e.position,
        e.movementGoal,
        infection = Some(Infection(severity, timestamp, durationDistribution.next()))
      )
    def maskReduction: Int = if e.hasMask then MASK_REDUCER else 1

  class ExternalInfectionLogic extends UpdateLogic:
    import it.unibo.pps.entity.common.Utils.*
    override def apply(env: Environment): Task[Environment] =
      for
        entities <- Task(env.externalEntities.select[InfectingEntity])
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
      yield env.update(externalEntities =
        env.externalEntities.filter(e => !infected.map(_.id).contains(e.id)) ++ infected.toSet
      )

  class InternalInfectionLogic extends UpdateLogic:
    import it.unibo.pps.entity.common.Utils.*
    override def apply(env: Environment): Task[Environment] =
      for
        structures <- Task(env.structures)
        infectedStructures <- Task(
          structures.filter(_.entities.map(_.entity).select[InfectingEntity].exists(_.infection.isDefined))
        )
        updatedStructures <- Task {
          for infectedStructure <- infectedStructures
          yield infectedStructure.updateEntitiesInside { entity =>
            entity
              .withCapabilities[InfectingEntity]
              .filter(_.infection.isEmpty)
              .map(e => InternalProbableInfection(env, e, infectedStructure))
              .filter(_.isHappening)
              .map(_.entity.infected(env.time, env.virus))
              .getOrElse(entity)
              .withCapabilities[SimulationEntity]
              .get
          }
        }
      yield env.update(structures = structures -- infectedStructures ++ updatedStructures)

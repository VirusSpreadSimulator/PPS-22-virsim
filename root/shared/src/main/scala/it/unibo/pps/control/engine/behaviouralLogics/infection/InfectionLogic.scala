package it.unibo.pps.control.engine.behaviouralLogics.infection

import it.unibo.pps.control.engine.behaviouralLogics.Logic.UpdateLogic
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import monix.eval.Task

object InfectionLogic:
  /** The reducer on the probability due to the mask */
  val MASK_REDUCER: Int = 2
  type InfectingEntity = SimulationEntity with Moving with Infectious

  case class ExternalProbableInfection(env: Environment, entity: InfectingEntity, infectors: Set[InfectingEntity])
  case class InternalProbableInfection(env: Environment, entity: InfectingEntity, structure: SimulationStructure)

  extension (e: InfectingEntity)
    def infected(virus: Virus): Task[InfectingEntity] =
      import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
      import it.unibo.pps.entity.entity.Infection.Severity
      import it.unibo.pps.entity.entity.Infection
      for
        severity <- Task(if virus.severeDeseaseProbability.isHappening then Severity.SERIOUS() else Severity.LIGHT())
        durationDistribution = GaussianIntDistribution(virus.averagePositivityDays, virus.stdDevPositivityDays)
        duration <- Task(durationDistribution.next())
        infection <- Task(Infection(severity, duration))
      yield BaseEntity(e.id, e.age, e.home, e.immunity, e.position, e.movementGoal, infection = Some(infection))
    def maskReduction: Int = if e.hasMask then MASK_REDUCER else 1

  class ExternalInfectionLogic extends UpdateLogic:
    import it.unibo.pps.entity.common.Utils.*
    override def apply(env: Environment): Task[Environment] =
      for
        entities <- Task(
          env.externalEntities.select[InfectingEntity].filter(_.movementGoal != Moving.MovementGoal.NO_MOVEMENT)
        ) // todo: dopo la modifica prendi solo le entità esterne
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
            .map(_.entity.infected(env.virus))
        }
      yield env.update(externalEntities =
        env.externalEntities.filter(e => !infected.map(_.id).contains(e.id)) ++ infected.toSet
      ) //todo: dopo la modifica sii sicuro che qui si aggiornano le entità che sono all'esterno

  class InternalInfectionLogic extends UpdateLogic:
    import it.unibo.pps.entity.common.Utils.*
    override def apply(env: Environment): Task[Environment] =
      for
        structures <- Task(env.structures)
        infected <- Task.sequence {
          structures
            .filter(_.entities.map(_.entity).select[InfectingEntity].exists(_.infection.isDefined))
            .flatMap(s =>
              s.entities
                .map(_.entity)
                .select[InfectingEntity]
                .filter(_.infection.isEmpty)
                .map(e => InternalProbableInfection(env, e, s))
            )
            .filter(_.isHappening)
            .map(_.entity.infected(env.virus))
          // todo: così le entitià in struttura non sono aggiornate.
          // todo: ribalta in modo che invece di ritornare le entità aggiornate, ritorni le strutture aggiornate, in quato ora le entità accessibili direttamente dall'env sono quelle che si trovano nell'ambiente esterno.
        }
      yield env.update(externalEntities =
        env.externalEntities.filter(e => !infected.map(_.id).contains(e.id)) ++ infected.toSet
      )
//todo: dopo la modifica sii sicuro che qui si aggiornano solo le strutture (che sono quelle nuove con i nuovi contagiati) + quelle vecchie che magari sono vuote o non hanno contagiati dentro e quindi non erano state considerate.

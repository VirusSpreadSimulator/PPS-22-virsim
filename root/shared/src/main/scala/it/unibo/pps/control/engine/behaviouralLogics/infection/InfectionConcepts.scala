package it.unibo.pps.control.engine.behaviouralLogics.infection

import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.virus.VirusComponent.Virus
import monix.eval.Task

object InfectionConcepts:
  /** The reducer on the probability due to the mask */
  val MASK_REDUCER: Int = 2
  type InfectingEntity = SimulationEntity with Moving with Infectious

  case class ExternalProbableInfection(
      env: Environment,
      entity: InfectingEntity,
      infectors: Set[InfectingEntity]
  )

  extension (e: InfectingEntity)
    def infected(virus: Virus): Task[InfectingEntity] =
      import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
      import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
      import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
      import it.unibo.pps.entity.entity.Infection.Severity
      import it.unibo.pps.entity.entity.Infection
      for
        severity <- Task(if virus.severeDeseaseProbability.isHappening then Severity.SERIOUS() else Severity.LIGHT())
        durationDistribution = GaussianIntDistribution(virus.averagePositivityDays, virus.stdDevPositivityDays)
        duration <- Task(durationDistribution.next())
        infection <- Task(Infection(severity, duration))
      yield BaseEntity(e.id, e.age, e.home, e.immunity, e.position, e.movementGoal, infection = Some(infection))

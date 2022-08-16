package it.unibo.pps.control.engine.behaviouralLogics.infection

import it.unibo.pps.control.engine.behaviouralLogics.Logic.UpdateLogic
import it.unibo.pps.control.engine.behaviouralLogics.infection.InfectionConcepts.{
  InfectingEntity,
  ExternalProbableInfection,
  infected
}
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.common.Utils.*
import monix.eval.Task

class ExternalInfectionLogic extends UpdateLogic:
  override def apply(env: Environment): Task[Environment] =
    for
      entities <- Task(env.entities.select[InfectingEntity])
      updatedEntities <- Task.sequence {
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
    yield env.update(entities = env.entities ++ updatedEntities.toSet)

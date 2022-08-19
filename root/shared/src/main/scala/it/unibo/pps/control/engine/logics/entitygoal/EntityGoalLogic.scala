package it.unibo.pps.control.engine.logics.entitygoal

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.environment.EnvironmentModule
import monix.eval.Task
import it.unibo.pps.entity.common.Time.Period
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import monocle.syntax.all.*

object EntityGoalLogic:
  class EntityGoalUpdateLogic extends UpdateLogic:
    override def apply(env: EnvironmentModule.Environment): Task[EnvironmentModule.Environment] = Task {
      env.time.period match
        case Period.START_DAY =>
          env.update(externalEntities =
            env.externalEntities.map(_.focus(_.movementGoal).replace(MovementGoal.RANDOM_MOVEMENT))
          )
        case Period.START_NIGHT =>
          env.update(externalEntities =
            env.externalEntities.map(
              _.focus(_.movementGoal).modify(oldGoal =>
                if GlobalDefaults.ENTITY_PROBABILITY_TO_RETURN_HOME.isHappening then MovementGoal.BACK_TO_HOME
                else oldGoal
              )
            )
          )
        case _ => env
    }

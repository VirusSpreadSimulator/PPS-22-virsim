package it.unibo.pps.control.engine.logics.entitygoal

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import it.unibo.pps.entity.common.Time.Period
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.common.Utils.*
import monocle.syntax.all.*

/** Module that contains the entity goal logic concepts. */
object EntityGoalLogic:
  /** Logic to update the goal of the external entities in the environment. */
  class EntityGoalUpdateLogic extends UpdateLogic:
    override def apply(env: Environment): Task[Environment] = Task {
      env.time.period match
        case Period.START_NIGHT =>
          env.update(externalEntities =
            env.externalEntities.map(_.andIf(_ => GlobalDefaults.ENTITY_PROBABILITY_TO_RETURN_HOME.isHappening) {
              _.focus(_.movementGoal).replace(MovementGoal.BACK_TO_HOME)
            })
          )
        case _ => env
    }

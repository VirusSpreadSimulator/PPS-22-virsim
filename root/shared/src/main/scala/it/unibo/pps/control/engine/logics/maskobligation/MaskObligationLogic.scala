package it.unibo.pps.control.engine.logics.maskobligation

import it.unibo.pps.control.engine.logics.Logic.{EventLogic, UpdateLogic}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.*
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.entity.Infection.Severity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import monocle.syntax.all._

object MaskObligationLogic:
  class SwitchMaskObligationLogic extends EventLogic:
    override def apply(environment: Environment): Task[Environment] =
      for
        environmentExternalUpdated <- handleMaskForExternalEntities(environment)
        environmentInternalUpdated <- handleMaskForInternalEntities(environmentExternalUpdated)
      yield environmentInternalUpdated

    def handleMaskForExternalEntities(environment: Environment): Task[Environment] =
      for
        structures <- Task(environment.structures)
        updatedStructures <- Task {
          for struct <- structures
          yield struct.updateEntitiesInside(entity => Some(entity.focus(_.hasMask).modify(!_)))
        }
      yield environment.update(structures = updatedStructures)

  def handleMaskForInternalEntities(env: Environment): Task[Environment] =
    for
      entities <- Task(env.externalEntities)
      updatedEntities <- Task(entities.map(_.focus(_.hasMask).modify(!_)))
    yield env.update(
      externalEntities = updatedEntities
    )

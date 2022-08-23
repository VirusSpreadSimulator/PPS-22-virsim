package it.unibo.pps.control.engine.logics.maskObligation

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
        environmentExtIntUpdated <- handleMaskForInternalEntities(environmentExternalUpdated)
      yield environmentExtIntUpdated

    def handleMaskForExternalEntities(environment: Environment): Task[Environment] =
      for
        structures <- Task(environment.structures)
        updatedStructures <- Task {
          for struct <- structures
          yield struct.updateEntitiesInside(entity => Some(handleMaskObligation(entity)))
        }
      yield environment.update(structures = updatedStructures)

  def handleMaskForInternalEntities(env: Environment): Task[Environment] =
    for
      entities <- Task(env.externalEntities)
      updatedEntities <- Task(entities.map(handleMaskObligation _))
    yield env.update(
      externalEntities = updatedEntities
    )
  def handleMaskObligation(entity: SimulationEntity): SimulationEntity =
    entity.focus(_.hasMask).modify(!_)

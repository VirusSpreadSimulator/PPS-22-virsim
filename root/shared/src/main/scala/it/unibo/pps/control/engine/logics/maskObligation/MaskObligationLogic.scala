package it.unibo.pps.control.engine.logics.maskObligation

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.*
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.entity.Infection.Severity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import monocle.syntax.all.*

object MaskObligationLogic:
  class AddMaskObligationLogic extends UpdateLogic:
    override def apply(environment: Environment): Task[Environment] =
      for
        environmentExternalUpdated <- handleMaskForExternalEntities(environment, true)
        environmentExtIntUpdated <- handleMaskForInternalEntities(environmentExternalUpdated, true)
      yield environmentExtIntUpdated

  class RemoveMaskObligationLogic extends UpdateLogic:
    override def apply(environment: Environment): Task[Environment] =
      for
        environmentExternalUpdated <- handleMaskForExternalEntities(environment, false)
        environmentExtIntUpdated <- handleMaskForInternalEntities(environmentExternalUpdated, false)
      yield environmentExtIntUpdated

  def handleMaskForExternalEntities(environment: Environment, obligation: Boolean): Task[Environment] =
    for
      structures <- Task(environment.structures)
      updatedStructures <- Task {
        for struct <- structures
        yield struct.updateEntitiesInside(entity => Some(handleMaskObligation(entity, obligation)))
      }
    yield environment.update(structures = updatedStructures)

  def handleMaskForInternalEntities(env: Environment, obligation: Boolean): Task[Environment] =
    for
      entities <- Task(env.externalEntities)
      updatedEntities <- Task(entities.map(handleMaskObligation(_, obligation)))
    yield env.update(
      externalEntities = updatedEntities
    )
  def handleMaskObligation(entity: SimulationEntity, obligation: Boolean): SimulationEntity =
    entity.focus(_.hasMask).replace(obligation)

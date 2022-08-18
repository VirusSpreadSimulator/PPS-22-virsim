package it.unibo.pps.control.engine.logics.entrance

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Visible
import it.unibo.pps.entity.structure.Structures.SimulationStructure

class EntranceLogic extends UpdateLogic:
  import it.unibo.pps.entity.common.Utils.*
  type MovingEntity = SimulationEntity with Moving
  type VisibleStructure = SimulationStructure with Visible

  override def apply(environment: Environment): Task[Environment] =
    for
      entities <- Task(environment.externalEntities.select[MovingEntity])
      entered <- Task {
        entities
          .filter(entity => checkIfIsInARay(entity, environment.structures.select[VisibleStructure]).isDefined)
          .filter(entity =>
            tryToEnter(
              entity,
              checkIfIsInARay(entity, environment.structures.select[VisibleStructure]).get,
              environment.time
            )
          )
      }
    yield environment.update(externalEntities =
      environment.externalEntities.filter(e => !entered.map(_.id).contains(e.id))
    )

  private def checkIfIsInARay(
      entity: MovingEntity,
      structures: Set[VisibleStructure]
  ): Option[VisibleStructure] =
    structures.find(structure => entity.position.distanceTo(structure.position) <= structure.visibilityDistance)

  private def tryToEnter(entity: MovingEntity, structure: VisibleStructure, timestamp: TimeStamp): Boolean =
    structure.tryToEnter(entity, timestamp).entities.map(_.entity).contains(entity)

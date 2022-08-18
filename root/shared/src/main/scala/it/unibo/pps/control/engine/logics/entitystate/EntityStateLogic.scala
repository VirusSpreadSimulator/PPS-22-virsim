package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.EntityComponent.{Infectious, Moving}
import monix.eval.Task
import monocle.syntax.all._

object EntityStateLogic:
  class UpdateEntityStateLogic extends UpdateLogic:
    override def apply(env: Environment): Task[Environment] = ???

  private object Updates:
    ???
/** Updates on entities health
  * @param entities
  *   the entities to update
  * @return
  *   a task of the set of entity that survived
  */
//    def updateEntitiesHealth(entities: Set[SimulationEntity]): Task[Set[SimulationEntity]] =
//      for
//        originalEntities <- Task(entities.select[BaseEntity])
//        updatedInfected <- Task {
//          originalEntities.filter(_.infection.isDefined).map(entity => entity.focus(_.h))
//        }
//      yield ()

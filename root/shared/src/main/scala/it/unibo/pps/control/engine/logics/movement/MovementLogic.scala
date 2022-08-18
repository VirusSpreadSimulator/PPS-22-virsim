package it.unibo.pps.control.engine.logics.movement

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Moving
import it.unibo.pps.entity.entity.EntityComponent.Infectious
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.entity.Infection
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.prolog.PrologNextMovement
import monix.eval.Task
import monocle.syntax.all._

class MovementLogic extends UpdateLogic:
  override def apply(environment: Environment): Task[Environment] =
//    val updatedEntities: Set[SimulationEntity] =
//      for
//        entity <- environment.externalEntities
//        id = entity.id
//        age = entity.age
//        home = entity.home
//        position = calculateNewPosition(entity.asInstanceOf[BaseEntity], environment.gridSide)
//        infection = entity.asInstanceOf[BaseEntity].infection
//        updatedEntity: SimulationEntity = BaseEntity(id, age, home, position = position, infection = infection)
//      yield updatedEntity
//
    for
      entities <- Task(environment.externalEntities)
      moved <- Task {
        entities.map(e => e.focus(_.position).replace(calculateNewPosition(e, environment.gridSide)))
      }
    yield environment.update(externalEntities = moved.select[SimulationEntity])

  /** Extracts random position given the set of possible position
    * @param set
    *   the set of possible position
    * @return
    *   a random position
    */
  private def extractRandomPosition(set: Set[Point2D]): Point2D =
    import util.Random
    set.iterator.drop(Random.nextInt(set.size)).next

  /** Check the goal of the movement of the entity to calculate the new position
    * @param entity
    *   the entity to update
    * @param gridSide
    *   the side of the grid of the world
    * @return
    *   the updated position of the entity
    */
  private def calculateNewPosition(entity: SimulationEntity, gridSide: Int): Point2D =
    entity.movementGoal match
      case MovementGoal.RANDOM_MOVEMENT =>
        extractRandomPosition(
          PrologNextMovement.calculateNextMovement(entity.position, gridSide, gridSide, 1)
        )
      case MovementGoal.BACK_TO_HOME =>
        extractRandomPosition(
          PrologNextMovement.calculateNextMovementToGoHome(
            entity.position,
            gridSide,
            gridSide,
            1,
            entity.home.position
          )
        )
      case _ => entity.position

package it.unibo.pps.control.engine.behaviouralLogics

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.prolog.PrologNextMovement

class CalculateNextMovement extends EntityLogic:
  override def execute(environment: Environment): Environment =

    val updatedEntities: Set[SimulationEntity] =
      for
        entity <- environment.entities
        id = entity.id
        age = entity.age
        home = entity.home
        position = extractRandomPosition(
          PrologNextMovement.calculateNextMovement(
            entity.asInstanceOf[BaseEntity].position,
            environment.gridSide,
            environment.gridSide,
            1
          )
        )
        infection = entity.asInstanceOf[BaseEntity].infection
        updatedEntity: SimulationEntity = BaseEntity(id, age, home, position = position, infection = infection)
      yield updatedEntity

    environment.initialized(environment.gridSide, updatedEntities, environment.virus, environment.structures)

  private def extractRandomPosition(set: Set[Point2D]): Point2D =
    import util.Random
    set.iterator.drop(Random.nextInt(set.size)).next

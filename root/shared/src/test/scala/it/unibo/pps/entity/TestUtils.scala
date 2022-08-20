package it.unibo.pps.entity

import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment

object TestUtils:
  extension (env: Environment)
    def internalEntities: Set[SimulationEntity] = env.structures.flatMap(_.entities).map(_.entity)

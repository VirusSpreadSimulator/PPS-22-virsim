package it.unibo.pps.entity

import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.*

object TestUtils:
  extension (env: Environment)
    def internalEntities: Set[SimulationEntity] = env.structures.flatMap(_.entities).map(_.entity)
    def hospitals: Set[SimulationStructure with Hospitalization] =
      env.structures.select[SimulationStructure with Hospitalization]

  extension (entities: Set[SimulationEntity])
    def totalHealth: Double = entities.toSeq.map(_.health).sum
    def totalImmunity: Double = entities.toSeq.map(_.immunity).sum

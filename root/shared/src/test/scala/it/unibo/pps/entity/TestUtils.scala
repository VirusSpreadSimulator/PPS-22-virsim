package it.unibo.pps.entity

import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.*

/** Module that contains some utils useful during testing. */
object TestUtils:
  extension (env: Environment)
    /** Obtain all the [[SimulationEntity]] that are inside all the [[SimulationStructure]] of an [[Environment]]. */
    def internalEntities: Set[SimulationEntity] = env.structures.flatMap(_.entities).map(_.entity)
    /** Obtain all the [[Hospitalization]] structures that are in the [[Environment]]. */
    def hospitals: Set[SimulationStructure with Hospitalization] =
      env.structures.select[SimulationStructure with Hospitalization]

  extension (entities: Set[SimulationEntity])
    /** Obtain the total health of a set of [[SimulationEntity]]. */
    def totalHealth: Double = entities.toSeq.map(_.health).sum
    /** Obtain the total immunity of a set of [[SimulationEntity]]. */
    def totalImmunity: Double = entities.toSeq.map(_.immunity).sum

  extension (structure: SimulationStructure)
    /** Utils that allow to execute [[SimulationStructure.tryToEnter()]] multiple times. */
    def tryToEnterMultiple(entities: Seq[SimulationEntity], timeStamp: TimeStamp): SimulationStructure =
      var s = structure
      for entity <- entities do s = s.tryToEnter(entity, timeStamp)
      s

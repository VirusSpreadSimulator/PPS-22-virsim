package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.select

object Extractor:

  /** The extractor is responsible to extract some statistics from the environment.
    * @tparam E
    *   The type of the extracted data.
    */
  trait DataExtractor[E]:
    def name: String
    def extractData(env: Environment): E

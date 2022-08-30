package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Habitable
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.select

/** All the extractors of statistics about entities. */
object EntitiesStats:

  /** The implementation of the alive entities statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Alive(override val name: String = "Alive") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int = env.allEntities.size

  /** The implementation of the dead entities statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Deaths(override val name: String = "Deaths") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int = env.deadEntities.size

  /** The implementation of the sick entities statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Sick(override val name: String = "Sick") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(entity => entity.health < GlobalDefaults.MIN_HEALTH_TO_GET_SICK)

  /** The implementation of the infected entities statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Infected(override val name: String = "Infected") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(_.infection.isDefined)

  /** The implementation of the healthy entities statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Healthy(override val name: String = "Healthy") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(_.health > GlobalDefaults.MIN_HEALTH_TO_GET_SICK)

  /** The implementation of the entities at home statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class AtHome(override val name: String = "At home") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures.select[SimulationStructure with Habitable].flatMap(_.entities).size

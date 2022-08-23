package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Habitable
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.select

object EntitiesStats:

  case class Alive(override val name: String = "Alive") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int = env.allEntities.size

  case class Deaths(override val name: String = "Deaths") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int = env.deadEntities.size

  case class Sick(override val name: String = "Sick") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(entity => entity.health < GlobalDefaults.MIN_HEALTH_TO_GET_SICK)

  case class Infected(override val name: String = "Infected") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(_.infection.isDefined)

  case class Healthy(override val name: String = "Healthy") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(_.health > GlobalDefaults.MIN_HEALTH_TO_GET_SICK)

  case class AtHome(override val name: String = "At home") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures.select[SimulationStructure with Habitable].map(_.entities).size

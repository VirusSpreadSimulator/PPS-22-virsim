package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment

object EnvironmentStats:

  case class Days(override val name: String = "Days") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.iteration

  case class Hours(override val name: String = "Hour") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.toHours

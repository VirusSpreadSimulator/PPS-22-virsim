package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.Time.TimeConfiguration.MINUTES_PER_HOUR

/** All the extractors of statistics about environment. */
object EnvironmentStats:

  case class Days(override val name: String = "Days") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.iteration

  case class Hours(override val name: String = "Hour") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.toHours

  case class Minutes(override val name: String = "Minutes") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.toMinutes - (MINUTES_PER_HOUR * env.time.toHours)

  case class Time(override val name: String = "Time") extends DataExtractor[String]:
    override def extractData(env: Environment): String =
      Hours().extractData(env).toString + ":" + Minutes().extractData(env).toString

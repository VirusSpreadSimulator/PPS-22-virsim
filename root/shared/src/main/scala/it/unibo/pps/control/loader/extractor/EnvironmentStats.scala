package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.Time.TimeConfiguration.MINUTES_PER_HOUR

/** All the extractors of statistics about environment. */
object EnvironmentStats:

  /** The implementation of the current days statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Days(override val name: String = "Days") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.iteration

  /** The implementation of the current hours statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Hours(override val name: String = "Hour") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.toHours

  /** The implementation of the current minutes statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Minutes(override val name: String = "Minutes") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.toMinutes - (MINUTES_PER_HOUR * env.time.toHours)

  /** The implementation of the Time statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Time(override val name: String = "Time") extends DataExtractor[String]:
    override def extractData(env: Environment): String =
      f"${Hours().extractData(env)}%02d:${Minutes().extractData(env)}%02d"

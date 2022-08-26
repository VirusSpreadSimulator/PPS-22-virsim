package it.unibo.pps.entity.common

import scala.annotation.targetName
import scala.concurrent.duration.{FiniteDuration, TimeUnit}

/** Module that contains the time-related concepts. */
object Time:
  /** Module that contain the time configuration for the simulation. */
  object TimeConfiguration:
    /** How many engine ticks are necessary for a minute in the virtual time of the simulation. */
    val TICKS_PER_MINUTE: Int = 1
    /** How many minutes there are per hour. */
    val MINUTES_PER_HOUR: Int = 60
    /** How many minutes there are per day. */
    val MINUTES_PER_DAY: Int = 1440
    /** It represents the number of minutes that split day/night. */
    val DAY_MINUTES_UPPER_BOUND: Long = 1000
    /** Based on [[MINUTES_PER_DAY]] and [[TICKS_PER_MINUTE]] represents how many engine ticks are needed in a day. */
    val TICKS_PER_DAY: Long = MINUTES_PER_DAY * TICKS_PER_MINUTE

  /** Alias for finite duration time in order to represent a duration in a more domain-specific terminology */
  type DurationTime = FiniteDuration
  /** Factory for [[DurationTime]]
    * @param length
    *   the length of the duration
    * @param unit
    *   the unit used to express the duration
    * @return
    *   [[DurationTime]] instance
    */
  def DurationTime(length: Long, unit: TimeUnit): DurationTime = FiniteDuration(length, unit)

  /** Describe the period of the day in order to understand if the environment time represent a day time or a night
    * time. See also [[TimeStamp.period]].
    */
  enum Period:
    /** It represents the first minute of the day. */
    case START_DAY
    /** It represents the day. */
    case DAY
    /** It represents the first minute of the night. */
    case START_NIGHT
    /** It represents the night. */
    case NIGHT

  /** Model the TimeStamp concept. */
  trait TimeStamp extends Ordered[TimeStamp]:
    /** Represent the ticks after the current iteration number.
      * @return
      *   the ticks after the current iteration number.
      */
    def relativeTicks: Long
    /** Represent the current iteration number.
      * @return
      *   the iteration number.
      */
    def iteration: Long
    /** Method to convert the ticks from the engine in the minutes of the virtual time. The minutes returned are respect
      * to the current iteration.
      * @return
      *   the minutes
      */
    def toMinutes: Long
    /** Method to convert the ticks from the engine in hours of the virtual time. The hours returned are respect to the
      * current iteration.
      * @return
      *   the hours
      */
    def toHours: Long
    /** Method to convert the current time to the period of the day.
      * @return
      *   the period of the day represented by the current time.
      */
    def period: Period

  object TimeStamp:
    def apply(relativeTicks: Long = 0, iteration: Long = 0): TimeStamp =
      // Create the instance applying a correction on the values provided by the user.
      TimeStampImpl(
        relativeTicks % TimeConfiguration.TICKS_PER_DAY,
        iteration + relativeTicks / TimeConfiguration.TICKS_PER_DAY
      )
    private case class TimeStampImpl(relativeTicks: Long, iteration: Long) extends TimeStamp:
      override def toMinutes: Long = relativeTicks / TimeConfiguration.TICKS_PER_MINUTE
      override def toHours: Long = toMinutes / TimeConfiguration.MINUTES_PER_HOUR
      override def period: Period = relativeTicks match
        case 0 => Period.START_DAY
        case TimeConfiguration.DAY_MINUTES_UPPER_BOUND => Period.START_NIGHT
        case t if t > TimeConfiguration.DAY_MINUTES_UPPER_BOUND => Period.NIGHT
        case _ => Period.DAY
      override def compare(that: TimeStamp): Int = iteration - that.iteration match
        case 0 => (relativeTicks - that.relativeTicks).toInt
        case notEqual => notEqual.toInt

  extension (t: TimeStamp)
    /** It allows to sum a [[DurationTime]] to a [[TimeStamp]]. It will apply the corrections on iteration
      * automatically.
      */
    @targetName("plus")
    def +(d: DurationTime): TimeStamp =
      TimeStamp(t.relativeTicks + d.toMinutes * TimeConfiguration.TICKS_PER_MINUTE, t.iteration)
    /** It allows to sum some absolute ticks to a [[TimeStamp]]. It will apply the corrections on iteration
      * automatically.
      */
    @targetName("plus")
    def +(ticks: Long): TimeStamp = TimeStamp(t.relativeTicks + ticks, t.iteration)

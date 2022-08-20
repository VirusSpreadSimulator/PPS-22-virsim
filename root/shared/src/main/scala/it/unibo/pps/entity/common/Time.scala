package it.unibo.pps.entity.common

import scala.annotation.targetName
import scala.concurrent.duration
import scala.concurrent.duration.{FiniteDuration, TimeUnit}

object Time:
  object TimeConfiguration:
    val TICKS_PER_MINUTE: Int = 1
    val MINUTES_PER_HOUR: Int = 60
    val MINUTES_PER_DAY: Int = 1440
    val DAY_MINUTES_UPPER_BOUND: Long = 1000
    val TICKS_PER_DAY: Long = MINUTES_PER_DAY * TICKS_PER_MINUTE

  /** Alias for finite duration time */
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
    * time.
    */
  enum Period:
    /** It represent the first minute of the day */
    case START_DAY
    /** It represent the day. */
    case DAY
    /** It represent the first minute of the night. */
    case START_NIGHT
    /** It represent the night. */
    case NIGHT

  /** Model the TimeStamp concept */
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
    private case class TimeStampImpl(override val relativeTicks: Long, override val iteration: Long) extends TimeStamp:
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
    @targetName("plus")
    def +(d: DurationTime): TimeStamp =
      TimeStamp(t.relativeTicks + d.toMinutes * TimeConfiguration.TICKS_PER_MINUTE, t.iteration)
    @targetName("plus")
    def +(ticks: Int): TimeStamp = TimeStamp(t.relativeTicks + ticks, t.iteration)

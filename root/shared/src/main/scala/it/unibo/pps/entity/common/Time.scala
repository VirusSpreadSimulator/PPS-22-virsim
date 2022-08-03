package it.unibo.pps.entity.common

import scala.concurrent.duration
import scala.concurrent.duration.{FiniteDuration, TimeUnit}

object Time:
  object TimeConfiguration:
    val TICKS_PER_MINUTE = 1
    val MINUTES_PER_DAY = 1440
    val DAY_MINUTES_UPPER_BOUND = 1000
    val TICKS_PER_DAY = MINUTES_PER_DAY * TICKS_PER_MINUTE

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

  /** Model the TimeStamp concept */
  trait TimeStamp extends Ordered[TimeStamp]:
    /** Ticks since the start of the simulation.
      * @return
      *   the absolute ticks.
      */
    def absoluteTicks: Long
    /** Represent the ticks after the current iteration number.
      * @return
      *   the ticks after the current iteration number.
      */
    def ticksSinceIterationStart: Long
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
    def toMinutes(): Long
    /** Method to convert the ticks from the engine in the minutes of the virtual time.The minutes returned are respect
      * to the current iteration
      * @return
      *   the absolute minutes
      */
    def toAbsoluteMinutes(): Long

  object TimeStamp:
    def apply(absoluteTicks: Long): TimeStamp =
      TimeStampImpl(absoluteTicks)
    private case class TimeStampImpl(override val absoluteTicks: Long) extends TimeStamp:
      override def ticksSinceIterationStart: Long = absoluteTicks - iteration * TimeConfiguration.TICKS_PER_DAY
      override def iteration: Long = absoluteTicks / TimeConfiguration.TICKS_PER_DAY
      override def toMinutes(): Long = ticksSinceIterationStart / TimeConfiguration.TICKS_PER_MINUTE
      override def toAbsoluteMinutes(): Long = absoluteTicks / TimeConfiguration.TICKS_PER_MINUTE
      override def compare(that: TimeStamp): Int = (this.absoluteTicks - that.absoluteTicks).toInt

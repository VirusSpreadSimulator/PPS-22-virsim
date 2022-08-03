package it.unibo.pps.entity.common

import scala.concurrent.duration
import scala.concurrent.duration.{FiniteDuration, TimeUnit}

object Time:
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
  trait TimeStamp:
    /** Represent the ticks after the current iteration number.
      * @return
      *   the ticks after the current iteration number.
      */
    def ticksSinceIterationStart: Int
    /** Represent the current iteration number.
      * @return
      *   the iteration number.
      */
    def iteration: Int
    /** Method to convert a timestamp to the ticks since the start of the simulation.
      * @return
      *   the absolute ticks.
      */
    def toAbsoluteTickValue(): Long
    /** Method to convert the ticks from the engine in the minutes of the virtual time. The minutes returned are respect
      * to the current iteration.
      * @return
      *   the minutes
      */
    def toMinutes(): Int
    /** Method to convert the ticks from the engine in the minutes of the virtual time.The minutes returned are respect
      * to the current iteration
      * @return
      *   the absolute minutes
      */
    def toAbsoluteMinutes(): Long

  object TimeStamp:
    def apply(ticksSinceIterationStart: Int, iteration: Int): TimeStamp =
      TimeStampImpl(ticksSinceIterationStart, iteration)
    private case class TimeStampImpl(override val ticksSinceIterationStart: Int, override val iteration: Int)
        extends TimeStamp:
      override def toAbsoluteTickValue(): Long = iteration * ticksSinceIterationStart
      override def toMinutes(): Int = ticksSinceIterationStart
      override def toAbsoluteMinutes(): Long = iteration * toMinutes()

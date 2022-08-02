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
    def ticksSinceIterationStart: Long
    /** Represent the current iteration number.
      * @return
      *   the iteration number.
      */
    def iteration: Int
    /** Method to convert a timestamp to the ticks since the start of the simulation.
      * @return
      *   the absolute ticks.
      */
    def toAbsoluteValue(): Long

  object TimeStamp:
    def apply(ticksSinceIterationStart: Long, iteration: Int): TimeStamp =
      TimeStampImpl(ticksSinceIterationStart, iteration)
    private case class TimeStampImpl(override val ticksSinceIterationStart: Long, override val iteration: Int)
        extends TimeStamp:
      override def toAbsoluteValue(): Long = iteration * ticksSinceIterationStart

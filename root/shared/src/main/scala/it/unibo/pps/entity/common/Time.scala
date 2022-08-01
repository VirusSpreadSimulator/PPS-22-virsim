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

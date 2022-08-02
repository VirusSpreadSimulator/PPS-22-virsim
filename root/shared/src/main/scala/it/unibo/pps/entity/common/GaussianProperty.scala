package it.unibo.pps.entity.common

import scala.concurrent.duration.{FiniteDuration, TimeUnit}
import scala.util.Random

object GaussianProperty:
  trait Gaussian[A]:
    def mean: Double
    def std: Double
    protected def nextGaussian(): Double = std * Random.nextGaussian() + mean
    def next(): A

  class GaussianDurationTime(override val mean: Double, override val std: Double, unit: TimeUnit)
      extends Gaussian[FiniteDuration]:
    override def next(): FiniteDuration = FiniteDuration(nextGaussian().toLong, unit)

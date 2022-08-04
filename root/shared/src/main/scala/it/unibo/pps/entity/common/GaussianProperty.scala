package it.unibo.pps.entity.common

import scala.concurrent.duration.{FiniteDuration, TimeUnit}
import scala.util.Random

object GaussianProperty:
  /** It represent a Gaussian Property.
    * @tparam A
    *   the type of the Property
    */
  trait Gaussian[A]:
    /** The mean of the gaussian distribution
      * @return
      *   the mean
      */
    def mean: Double
    /** The standard deviation of the gaussian distribution
      * @return
      *   the standard deviation
      */
    def std: Double
    /** Method that allow to generate the next gaussian that follow the distribution
      * @return
      *   the generated number
      */
    protected def nextGaussian(): Double = std * Random.nextGaussian() + mean
    /** Method that allow to generate the next value for the property of type [[A]].
      * @return
      *   the generated property value
      */
    def next(): A

  /** A duration time generator that follow a gaussian distribution
    * @param mean
    *   the mean of the gaussian distribution
    * @param std
    *   the standard deviation of the gaussian distribution
    * @param unit
    *   the unit choosen for the duration time object
    */
  class GaussianDurationTime(override val mean: Double, override val std: Double, unit: TimeUnit)
      extends Gaussian[FiniteDuration]:
    override def next(): FiniteDuration = FiniteDuration(nextGaussian().toLong, unit)

  class GaussianAgeDistribution(override val mean: Double, override val std: Double) extends Gaussian[Int]:
    override def next(): Int = nextGaussian().toInt

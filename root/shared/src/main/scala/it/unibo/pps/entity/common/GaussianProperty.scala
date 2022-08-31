package it.unibo.pps.entity.common

import it.unibo.pps.entity.common.Time.DurationTime
import scala.concurrent.duration.TimeUnit
import scala.util.Random

object GaussianProperty:
  /** It represent a Gaussian Property.
    * @tparam A
    *   the type of the Property
    */
  trait Gaussian[A]:
    /** The mean of the gaussian distribution.
      * @return
      *   the mean
      */
    def mean: Double
    /** The standard deviation of the gaussian distribution.
      * @return
      *   the standard deviation
      */
    def std: Double
    /** Method that allow to generate the next value for the property of type [[A]].
      * @return
      *   the generated property value
      */
    def next(): A = convert(nextGaussian())
    /** Method to implement the conversion from the generated number to the required gaussian type [[A]] */
    protected def convert(next: Double): A
    /** Method that allow to generate the next gaussian that follow the distribution.
      * @return
      *   the generated number
      */
    private def nextGaussian(): Double = std * Random.nextGaussian() + mean

  /** A duration time generator that follow a gaussian distribution.
    * @param mean
    *   the mean of the gaussian distribution
    * @param std
    *   the standard deviation of the gaussian distribution
    * @param unit
    *   the unit chosen for the duration time object
    */
  case class GaussianDurationTime(mean: Double, std: Double, unit: TimeUnit) extends Gaussian[DurationTime]:
    override protected def convert(next: Double): DurationTime = DurationTime(next.toLong, unit)

  /** A integer generator that follow a gaussian distribution.
    * @param mean
    *   the mean of the gaussian distribution
    * @param std
    *   the standard deviation of the gaussian distribution
    */
  case class GaussianIntDistribution(mean: Double, std: Double) extends Gaussian[Int]:
    override protected def convert(next: Double): Int = next.toInt

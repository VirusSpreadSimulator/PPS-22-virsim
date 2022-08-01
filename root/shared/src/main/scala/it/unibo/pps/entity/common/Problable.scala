package it.unibo.pps.entity.common

import scala.util.Random

object ProblableEvents:
  /** Model the result a probable event that can happen or not */
  enum ProbabilityResult:
    case HAPPENED, NOTHAPPENED

  object ProbabilityResult:
    given Conversion[Boolean, ProbabilityResult] with
      def apply(result: Boolean): ProbabilityResult = if result then HAPPENED else NOTHAPPENED

  /** Type-class for extending a type that can act as a probable event
    * @tparam E
    *   the type that model the event
    */
  trait Probable[E]:
    extension (e: E) def probability: Double

  /** A set of operations modeled on the Probable type-class */
  object ProbableOps:
    extension [E: Probable](e: E) def isHappening: ProbabilityResult = e.probability >= Random.nextDouble()

  /** Object that group the main given of Probable type-class */
  object ProbableGivenInstance:
    given Probable[Int] with
      extension (e: Int) def probability: Double = e
    given Probable[Double] with
      extension (e: Double) def probability: Double = e

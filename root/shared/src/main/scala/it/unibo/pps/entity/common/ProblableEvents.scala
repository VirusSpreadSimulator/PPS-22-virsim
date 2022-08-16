package it.unibo.pps.entity.common

import it.unibo.pps.control.engine.behaviouralLogics.infection.InfectionConcepts.ExternalProbableInfection
import scala.util.Random

object ProblableEvents:
  /** Model the result a probable event that can happen or not */
  enum ProbabilityResult:
    case HAPPENED, NOTHAPPENED

  object ProbabilityResult:
    given Conversion[Boolean, ProbabilityResult] with
      def apply(result: Boolean): ProbabilityResult = if result then HAPPENED else NOTHAPPENED
    given Conversion[ProbabilityResult, Boolean] with
      def apply(result: ProbabilityResult): Boolean = result match
        case HAPPENED => true
        case _ => false

  /** Type-class for extending a type that can act as a probable event
    * @tparam E
    *   the type that model the event
    */
  trait Probable[E]:
    /** Method in which it's implemented the formula that express the probability of the interested event */
    extension (e: E) def probability: Double

  /** A set of operations modeled on the Probable type-class */
  object ProbableOps:
    /** Method that handle the probability and every time that is called return a [[ProbabilityResult]] that describe if
      * the event is happened or not.
      */
    extension [E: Probable](e: E) def isHappening: ProbabilityResult = e.probability >= Random.nextDouble()

  /** Object that group the main given of Probable type-class */
  object ProbableGivenInstance:
    given Probable[Int] with
      extension (e: Int) def probability: Double = e
    given Probable[Double] with
      extension (e: Double) def probability: Double = e
    given Probable[ExternalProbableInfection] with
      extension (inf: ExternalProbableInfection)
        def probability: Double = inf.infectors.foldLeft(0.0)((acc, infector) =>
          acc + (inf.env.virus.spreadRate * (1 - inf.entity.position.distanceTo(
            infector.position
          ) / inf.env.virus.maxInfectionDistance) * (1 - inf.entity.immunity)) //todo: divide per mask)
        )

package it.unibo.pps.entity.common

import it.unibo.pps.control.engine.logics.infection.InfectionLogic.{
  ExternalProbableInfection,
  InternalProbableInfection,
  maskReduction
}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{MAX_VALUES, VirusDefaults}
import scala.util.Random

/** This module contains the concepts related to events that could occur with a probability. */
object ProblableEvents:
  /** Model the result a probable event that can happen or not */
  enum ProbabilityResult:
    case HAPPENED, NOTHAPPENED

  object ProbabilityResult:
    /** Conversion from boolean to [[ProbabilityResult]] */
    given Conversion[Boolean, ProbabilityResult] with
      def apply(result: Boolean): ProbabilityResult = if result then HAPPENED else NOTHAPPENED
    /** Conversion fron [[ProbabilityResult]] to boolean */
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
    /** Obtain a probable event from a integer. For example 90.isHappening is an event with the 90% probability of
      * happening.
      */
    given Probable[Int] with
      extension (e: Int) def probability: Double = e
    /** Obtain a probable event from a double. For example (50.4).isHappening is an event with the 50.4% probability of
      * happening.
      */
    given Probable[Double] with
      extension (e: Double) def probability: Double = e
    /** Augment the [[ExternalProbableInfection]] with it's probability of occurring */
    given Probable[ExternalProbableInfection] with
      extension (inf: ExternalProbableInfection)
        def probability: Double = inf.infectors.foldLeft(0.0)((acc, infector) =>
          acc + (inf.env.virus.spreadRate * (1 - inf.entity.position.distanceTo(
            infector.position
          ) / inf.env.virus.maxInfectionDistance) * (1 - inf.entity.immunity / MAX_VALUES.MAX_IMMUNITY)) / (inf.entity.maskReduction * infector.maskReduction)
        )
    /** Augment the [[InternalProbableInfection]] with it's probability of occurring */
    given Probable[InternalProbableInfection] with
      extension (inf: InternalProbableInfection)
        def probability: Double =
          val infectedInside =
            inf.structure.entities.map(_.entity).filter(_.infection.isDefined)
          if infectedInside.nonEmpty then
            ((inf.env.virus.spreadRate * inf.structure.infectionProbability * (1 - inf.entity.immunity / MAX_VALUES.MAX_IMMUNITY)) /
              (inf.entity.maskReduction * Math
                .max(1, VirusDefaults.MASK_REDUCER * (infectedInside.count(_.hasMask) / infectedInside.size)))) *
              (infectedInside.size / (inf.structure.entities.size - 1))
          else 0

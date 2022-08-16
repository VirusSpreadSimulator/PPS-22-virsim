package it.unibo.pps.entity.virus

import it.unibo.pps.control.loader.configuration.SimulationDefaults.VirusDefaults

object VirusComponent:

  trait VirusConfiguration:

    /** The name of the virus inside the environment.
      * @return
      *   the name of the virus.
      */
    def name: String

    /** The ability of the virus to spread inside the population.
      * @return
      *   the spread rate.
      */
    def spreadRate: Double

    /** The average duration of the virus infection.
      * @return
      *   the average days of infection.
      */
    def averagePositivityDays: Int

    /** The Standard Deviation of positivity days.
      * @return
      *   the Standard Deviation of positivity days.
      */
    def stdDevPositivityDays: Double

    /** The ability of the virus to cause health damages.
      * @return
      *   the desease probability of the virus.
      */
    def severeDeseaseProbability: Double

    /** Maximum distance of infection
      * @return
      *   the maximum distance.
      */
    def maxInfectionDistance: Double

  case class Virus(
      override val name: String = VirusDefaults.NAME,
      override val spreadRate: Double = VirusDefaults.SPREAD_RATE,
      override val averagePositivityDays: Int = VirusDefaults.AVERAGE_POSITIVITY_DAYS,
      override val stdDevPositivityDays: Double = VirusDefaults.STD_DEV_POSITIVITY_DAYS,
      override val severeDeseaseProbability: Double = VirusDefaults.SEVERE_DESEASE_PROBABILITY,
      override val maxInfectionDistance: Double = VirusDefaults.MAXIMUM_INFECTION_DISTANCE
  ) extends VirusConfiguration

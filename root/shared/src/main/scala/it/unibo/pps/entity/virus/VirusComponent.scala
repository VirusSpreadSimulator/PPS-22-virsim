package it.unibo.pps.entity.virus

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

    /** The ability of the virus to cause health damages.
      * @return
      *   the desease probability of the virus.
      */
    def severeDeseaseProbability: Int

  case class Virus(
      override val name: String = "Generic-Virus",
      override val spreadRate: Double = 1.5,
      override val averagePositivityDays: Int = 7,
      override val severeDeseaseProbability: Int = 25
  ) extends VirusConfiguration

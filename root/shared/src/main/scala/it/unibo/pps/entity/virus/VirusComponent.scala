package it.unibo.pps.entity.virus

object VirusComponent:

  trait VirusConfiguration:

    /** The name of the virus inside the environment.
      * @return
      *   the name of the virus.
      */
    def getVirusName: Option[String]

    /** The ability of the virus to spread inside the population.
      * @return
      *   the spread rate.
      */
    def getSpreadRate: Option[Double]

    /** The average duration of the virus infection.
      * @return
      *   the average days of infection.
      */
    def getAveragePositivityDays: Option[Int]

    /** The ability of the virus to cause health damages.
      * @return
      *   the desease probability of the virus.
      */
    def getSevereDeseaseProbability: Option[Int]

  class Virus extends VirusConfiguration:

    var name: Option[String] = Some("Generic-Virus")
    var spreadRate: Option[Double] = Some(1.5)
    var averagePositivityDays: Option[Int] = Some(7)
    var severeDeseaseProbability: Option[Int] = Some(35)

    override def getVirusName: Option[String] = name

    override def getSpreadRate: Option[Double] = spreadRate

    override def getAveragePositivityDays: Option[Int] = averagePositivityDays

    override def getSevereDeseaseProbability: Option[Int] = severeDeseaseProbability

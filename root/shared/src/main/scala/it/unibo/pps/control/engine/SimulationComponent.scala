package it.unibo.pps.control.engine

object SimulationComponent:

  trait SimulationConfiguration:

    /** @return the duration of the simulation. */
    def getDuration: Option[Int]

    /** @return the number of entities choosed by the user. */
    def getNumberOfEntities: Option[Int]

    /** Default: 4
      * @return
      *   the number of people at every house.
      */
    def getPeoplePerHouse: Option[Int]

    /** @return */
    def getAveragePopulationAge: Option[Int]

    def getStdDevPopulationAge: Option[Double]

    def getStartingInfectedPercentage: Option[Double]

  class Simulation extends SimulationConfiguration:

    var duration: Option[Int] = None
    var numberOfEntities: Option[Int] = None
    var peoplePerHouse: Option[Int] = Some(4)
    var averagePopulationAge: Option[Int] = Some(40)
    var stdDevPopulationAge: Option[Double] = Some(0.5)
    var startingInfectedPercentage: Option[Double] = Some(10)

    override def getDuration: Option[Int] = duration
    override def getNumberOfEntities: Option[Int] = numberOfEntities
    override def getPeoplePerHouse: Option[Int] = peoplePerHouse
    override def getAveragePopulationAge: Option[Int] = averagePopulationAge
    override def getStdDevPopulationAge: Option[Double] = stdDevPopulationAge
    override def getStartingInfectedPercentage: Option[Double] = startingInfectedPercentage

package it.unibo.pps.control.loader.configuration

object SimulationConfigurations:
  extension (simulation: SimulationConfiguration)
    def days(numberOfDays: Int): SimulationConfiguration = WithDuration(Some(numberOfDays), None)
    def entities(numberOfEntities: Int): SimulationConfiguration =
      WithCardinality(simulation.duration, Some(numberOfEntities))

  trait SimulationConfiguration:

    def duration: Option[Int]
    def cardinality: Option[Int]
    def peoplePerHouse: Int
    def averagePopulationAge: Int
    def stdDevPopulationAge: Double
    def startingInfectedPercentage: Double

  trait DefaultSimulationConfiguration extends SimulationConfiguration:

    override def peoplePerHouse: Int = 4
    override def averagePopulationAge: Int = 50
    override def stdDevPopulationAge: Double = 0.5
    override def startingInfectedPercentage: Double = 10

  case class WithDuration(override val duration: Option[Int], override val cardinality: Option[Int])
      extends DefaultSimulationConfiguration

  case class WithCardinality(override val duration: Option[Int], override val cardinality: Option[Int])
      extends DefaultSimulationConfiguration

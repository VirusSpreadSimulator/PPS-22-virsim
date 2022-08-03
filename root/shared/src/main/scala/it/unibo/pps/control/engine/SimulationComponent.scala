package it.unibo.pps.control.engine

object SimulationComponent:

  trait SimulationConfiguration:

    /** @return the size of the environment choosed by the user. */
    def gridSide: Int

    /** @return the duration of the simulation. */
    def duration: Int

    /** @return the number of entities choosed by the user. */
    def numberOfEntities: Int

    /** Default: 4
      * @return
      *   the number of people at every house.
      */
    def peoplePerHouse: Int

    /** @return
      *   the average age of the entities inside the environment.
      */
    def averagePopulationAge: Int

    /** @return
      *   the age standard deviation of the entities inside the environment.
      */
    def stdDevPopulationAge: Double

    /** @return the initial percentage of infected entities inside the environment. */
    def startingInfectedPercentage: Double

  case class Simulation(
      override val gridSide: Int = 50,
      override val duration: Int = 30,
      override val numberOfEntities: Int = 100,
      override val peoplePerHouse: Int = 4,
      override val averagePopulationAge: Int = 40,
      override val stdDevPopulationAge: Double = 0.5,
      override val startingInfectedPercentage: Double = 10
  ) extends SimulationConfiguration

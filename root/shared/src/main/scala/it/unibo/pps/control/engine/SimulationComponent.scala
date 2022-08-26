package it.unibo.pps.control.engine

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults

object SimulationComponent:

  /** The parameters of the simulation configurable by the user. */
  trait SimulationParameters:

    /** @return the size of the environment chose by the user. */
    def gridSide: Int

    /** @return the duration of the simulation. */
    def duration: Int

    /** @return the number of entities chose by the user. */
    def numberOfEntities: Int

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
      override val gridSide: Int = GlobalDefaults.GRID_SIDE,
      override val duration: Int = GlobalDefaults.DURATION,
      override val numberOfEntities: Int = GlobalDefaults.NUMBER_OF_ENTITIES,
      override val averagePopulationAge: Int = GlobalDefaults.AVERAGE_POPULATION_AGE,
      override val stdDevPopulationAge: Double = GlobalDefaults.STD_DEV_POPULATION_AGE,
      override val startingInfectedPercentage: Double = GlobalDefaults.STARTING_INFECTED_PERCENTAGE
  ) extends SimulationParameters

package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.control.engine.SimulationComponent.Simulation

object SimulationDSL:

  extension (simulation: Simulation)
    def days(numberOfDays: Int): Simulation =
      simulation.duration = Some(numberOfDays)
      simulation
    def entities(numberOfEntities: Int): Simulation =
      simulation.numberOfEntities = Some(numberOfEntities)
      simulation
    def averagePopulationAge(age: Int): Simulation =
      simulation.averagePopulationAge = Some(age)
      simulation
    def stdDevPopulationAge(standardDeviation: Double): Simulation =
      simulation.stdDevPopulationAge = Some(standardDeviation)
      simulation
    def peoplePerHouse(numberOfPeople: Int): Simulation =
      simulation.peoplePerHouse = Some(numberOfPeople)
      simulation
    def startingInfectedPercentage(infectedPeople: Int): Simulation =
      simulation.startingInfectedPercentage = Some(infectedPeople)
      simulation

package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import monocle.syntax.all.*

/** The DSL of the simulation for the configuration file. */
object SimulationDSL:

  def simulation: Simulation = Simulation()

  extension (sim: Simulation)
    def gridSide(size: Int): Simulation = sim.focus(_.gridSide).replace(size)
    def days(numberOfDays: Int): Simulation = sim.focus(_.duration).replace(numberOfDays)
    def entities(numberOfEntities: Int): Simulation = sim.focus(_.numberOfEntities).replace(numberOfEntities)
    def averagePopulationAge(age: Int): Simulation = sim.focus(_.averagePopulationAge).replace(age)
    def stdDevPopulationAge(standardDeviation: Double): Simulation =
      sim.focus(_.stdDevPopulationAge).replace(standardDeviation)
    def startingInfectedPercentage(infectedPeople: Int): Simulation =
      sim.focus(_.startingInfectedPercentage).replace(infectedPeople)

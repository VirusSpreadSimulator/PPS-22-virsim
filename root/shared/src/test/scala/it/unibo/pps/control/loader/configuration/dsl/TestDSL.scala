package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration}
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*
import it.unibo.pps.entity.structure.Structures
import it.unibo.pps.entity.structure.Structures.GenericBuilding
import it.unibo.pps.entity.virus.VirusComponent.Virus
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TestDSL extends AnyFunSuite with Matchers:

  test("Simulation should be configurable via DSL") {
    "simulation gridSide 50 days 7 entities 200 averagePopulationAge 40 stdDevPopulationAge 0.5 peoplePerHouse 4 startingInfectedPercentage 10" should compile
  }

  test("Virus should be configurable via DSL") {
    "virus spreadRate 1.5 averagePositivityDays 7 severeDeseaseProbability 15" should compile
  }

  test("Structures should be configurable via DSL") {
    "structures are GenericBuilding(infectionProbability = 20, capacity = 30, position = (10L, 10L)) " +
      "and GenericBuilding(infectionProbability = 20, capacity = 30, position = (10L, 10L))" should compile

  }

  test("Entire environment should be configurable via DSL") {
    "VirsimConfiguration(" +
      "simulation gridSide 50 days 7 entities 200 averagePopulationAge 40 stdDevPopulationAge 0.5 peoplePerHouse 4 startingInfectedPercentage 10, " +
      "virus spreadRate 1.5 averagePositivityDays 7 severeDeseaseProbability 15, " +
      "structures are GenericBuilding(position = (10L, 10L), infectionProbability = 20, capacity = 30) and " +
      "GenericBuilding(position = (20L, 20L), infectionProbability = 25, capacity = 40))" should compile
  }

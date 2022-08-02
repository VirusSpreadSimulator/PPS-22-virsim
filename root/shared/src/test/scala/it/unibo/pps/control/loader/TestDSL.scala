package it.unibo.pps.control.loader

import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration}
import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.entity.virus.VirusComponent.Virus
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*

import scala.language.postfixOps

class TestDSL extends AnyFunSuite with Matchers:

  private val simulation: Simulation = Simulation()
  private val virus: Virus = Virus()

  test("Simulation should be configurable via DSL") {
    simulation days 5 entities 200 averagePopulationAge 40 stdDevPopulationAge 0.5 peoplePerHouse 4 startingInfectedPercentage 10
  }

  test("Virus should be configurable via DSL") {
    virus name "Covid-19" spreadRate 1.5 averagePositivityDays 7 severeDeseaseProbability 15
  }

  test("Structures should be configurable via DSL") {
    ???
  }

  test("Entire environment should be configurable via DSL") {
    VirsimConfiguration(
      Simulation() days 5 entities 200 averagePopulationAge 40 stdDevPopulationAge 0.5 peoplePerHouse 4 startingInfectedPercentage 10,
      Virus() name "Covid-19" spreadRate 1.5 averagePositivityDays 7 severeDeseaseProbability 15,
      ???
    )
  }

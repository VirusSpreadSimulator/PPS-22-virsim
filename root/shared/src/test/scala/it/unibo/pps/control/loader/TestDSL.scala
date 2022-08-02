package it.unibo.pps.control.loader

import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration}
import it.unibo.pps.control.engine.SimulationComponent.Simulation
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.control.loader.configuration.ConfigurationDSL.*

import scala.language.postfixOps

class TestDSL extends AnyFunSuite with Matchers:

  test("Simulation should be configurable via DSL") {
    Simulation() days 5 entities 200 averagePopulationAge 40 stdDevPopulationAge 0.5 peoplePerHouse 4 startingInfectedPercentage 10
  }

  test("Virus should be configurable via DSL") {
    ???
  }

  test("Structures should be configurable via DSL") {
    ???
  }

  test("Entire environment should be configurable via DSL") {
    VirsimConfiguration(
      Simulation() days 5 entities 200 averagePopulationAge 40 stdDevPopulationAge 0.5 peoplePerHouse 4 startingInfectedPercentage 10,
      ???,
      ???
    )
  }

package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.virus.VirusComponent.Virus
import monocle.syntax.all.*

/** The DSL of the simulation virus for the configuration file. */
object VirusDSL:

  def virus: Virus = Virus()

  extension (virus: Virus)

    def virusName(virusName: String): Virus =
      virus.focus(_.name).replace(virusName)

    def spreadRate(virusSpreadRate: Double): Virus =
      virus.focus(_.spreadRate).replace(virusSpreadRate)

    def averagePositivityDays(days: Int): Virus =
      virus.focus(_.averagePositivityDays).replace(days)

    def stdDevPositivityDays(stdDev: Double): Virus =
      virus.focus(_.stdDevPositivityDays).replace(stdDev)

    def severeDeseaseProbability(probability: Double): Virus =
      virus.focus(_.severeDeseaseProbability).replace(probability)

    def maxInfectionDistance(distance: Double): Virus =
      virus.focus(_.maxInfectionDistance).replace(distance * GlobalDefaults.GRID_MULTIPLIER)

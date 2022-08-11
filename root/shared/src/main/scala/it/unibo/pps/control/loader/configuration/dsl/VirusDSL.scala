package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.entity.virus.VirusComponent.Virus
import monocle.syntax.all.*

object VirusDSL:

  def virus: Virus = Virus()

  extension (virus: Virus)

    def name(virusName: String): Virus =
      virus.focus(_.name).replace(virusName)

    def spreadRate(virusSpreadRate: Double): Virus =
      virus.focus(_.spreadRate).replace(virusSpreadRate)

    def averagePositivityDays(days: Int): Virus =
      virus.focus(_.averagePositivityDays).replace(days)

    def stdDevPositivityDays(stdDev: Double): Virus =
      virus.focus(_.stdDevPositivityDays).replace(stdDev)

    def severeDeseaseProbability(probability: Int): Virus =
      virus.focus(_.severeDeseaseProbability).replace(probability)

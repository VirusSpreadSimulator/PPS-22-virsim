package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.entity.virus.VirusComponent.Virus

object VirusDSL:

  extension (virus: Virus)

    def name(virusName: String): Virus =
      virus.name = Some(virusName)
      virus

    def spreadRate(virusSpreadRate: Double): Virus =
      virus.spreadRate = Some(virusSpreadRate)
      virus

    def averagePositivityDays(days: Int): Virus =
      virus.averagePositivityDays = Some(days)
      virus

    def severeDeseaseProbability(probability: Int): Virus =
      virus.severeDeseaseProbability = Some(probability)
      virus

package it.unibo.pps.control.loader.configuration

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.structure.StructureComponent.Structure

object ConfigurationComponent:

  trait Configuration:

    /** The section of the configuration file about the simulation parameters.
      * @return
      *   a configuration of the simulation.
      */
    def simulation: Simulation

    /** The section of the configuration file about the virus parameters.
      * @return
      *   a configuration of the virus.
      */
    def virusConfiguration: Virus

    /** The section of the configuration file about the structures parameters.
      * @return
      *   a configuration of the structures.
      */
    def structuresConfiguration: Set[Structure]

  case class VirsimConfiguration(
      override val simulation: Simulation,
      override val virusConfiguration: Virus,
      override val structuresConfiguration: Set[Structure]
  ) extends Configuration

package it.unibo.pps.control.loader.configuration

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.control.loader.configuration.ConfigurationParser

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

object ConfigurationComponent:

  given ScriptEngine = new javax.script.ScriptEngineManager(getClass.getClassLoader).getEngineByName("scala")
  given ConfigurationParser = ConfigurationParser()

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
    def structuresConfiguration: Set[SimulationStructure]

  enum ConfigurationResult:
    case OK(configuration: Configuration)
    case ERROR(errors: List[ConfigurationError])

  enum ConfigurationError:
    case WRONG_PARAMETER(message: String)
    case INVALID_FILE(message: String)

  case class VirsimConfiguration(
      override val simulation: Simulation,
      override val virusConfiguration: Virus,
      override val structuresConfiguration: Set[SimulationStructure]
  ) extends Configuration

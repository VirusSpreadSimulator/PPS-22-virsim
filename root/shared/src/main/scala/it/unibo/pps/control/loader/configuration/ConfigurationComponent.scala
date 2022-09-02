package it.unibo.pps.control.loader.configuration

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.entity.EntityFactory

object ConfigurationComponent:

  /** The instance of the entity factory used by the loader to create environment.
    * @return
    *   the entity factory.
    */
  given EntityFactory = EntityFactory()

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

  /** The result of configuration parsing */
  enum ConfigurationResult:
    case OK(configuration: Configuration)
    case ERROR(errors: List[ConfigurationError])

  /** All possible configuration errors. */
  enum ConfigurationError:
    case WRONG_PARAMETER(message: String)
    case INVALID_FILE(message: String)

  case class VirsimConfiguration(
      override val simulation: Simulation,
      override val virusConfiguration: Virus,
      override val structuresConfiguration: Set[SimulationStructure]
  ) extends Configuration

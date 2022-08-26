package it.unibo.pps.control.parser

import it.unibo.pps.control.engine.EngineModule.Requirements
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{
  Configuration,
  ConfigurationError,
  ConfigurationResult
}
import it.unibo.pps.control.parser.ReaderModule
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{GlobalDefaults, MAX_VALUES, MIN_VALUES}
import it.unibo.pps.control.parser.ReaderModule.FilePath
import monix.eval.Task

object ParserModule:

  extension [A](parameter: Ordered[A])
    def shouldBeWithin(range: (A, A)): Boolean = parameter >= range._1 && parameter <= range._2

  extension (bool: Boolean)
    def andIfNot(message: String): List[ConfigurationError] =
      if !bool then List(ConfigurationError.WRONG_PARAMETER(message)) else List.empty

  /** Interface that defines the configuration parser. All the parsers need to extend this trait in order to be
    * compatible. Every module (jvm, js) has to implement its own configuration parser.
    */
  trait Parser:

    /** @param path
      *   The path of the file.
      * @return
      *   The content of the file as a String.
      */
    def readFile(filePath: FilePath): Task[String]

    /** @param program
      *   The configuration file.
      * @return
      *   an Option of the configuration if there are no errors, or a None otherwise.
      */
    def loadConfiguration(program: String): Task[Option[Configuration]]

    /** @param configuration
      *   The simulation parameters of the configuration.
      * @return
      *   the result of configuration errors checking.
      */
    def checkErrors(configuration: Configuration): Task[ConfigurationResult] =
      for
        errors <- Task {
          (configuration.simulation.gridSide shouldBeWithin (MIN_VALUES.MIN_GRID_SIZE, MAX_VALUES.MAX_GRID_SIZE) andIfNot "Error: invalid parameter gridSide!") :::
            (configuration.simulation.numberOfEntities shouldBeWithin (MIN_VALUES.MIN_NUMBER_OF_ENTITIES, MAX_VALUES.MAX_NUMBER_OF_ENTITIES) andIfNot "Error: invalid parameter numberOfEntities!") :::
            (configuration.virusConfiguration.severeDeseaseProbability shouldBeWithin (0, 1) andIfNot "Error: probability must be in range (0, 1)!") :::
            (configuration.virusConfiguration.spreadRate shouldBeWithin (0, 1) andIfNot "Error: spreadRate must be in range (0, 1)!") :::
            (configuration.structuresConfiguration.forall(struc =>
              struc.position.x < configuration.simulation.gridSide &&
                struc.position.y < configuration.simulation.gridSide
            ) andIfNot "Error: invalid structure position!") :::
            (configuration.structuresConfiguration
              .map(_.position)
              .size == configuration.structuresConfiguration.size andIfNot "Error: multiples structures in same position !")

        }
        result <- errors.size match
          case 0 => Task(ConfigurationResult.OK(configuration))
          case _ => Task(ConfigurationResult.ERROR(errors))
      yield result

  /** Provider of the component */
  trait Provider:
    /** The current parser of the configuration */
    val parser: Parser

  trait Interface extends Provider

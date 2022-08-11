package it.unibo.pps.control.loader.configuration

import monix.eval.Task
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MAX_VALUES
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MIN_VALUES

import javax.script.{ScriptEngine, ScriptException}
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{
  Configuration,
  ConfigurationError,
  ConfigurationResult,
  VirsimConfiguration,
  given
}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults

import scala.io.Source

trait ConfigurationParser:

  extension (parameter: Int)
    def shouldBeWithin(range: (Int, Int)): Boolean = parameter >= range._1 && parameter <= range._2

  extension (bool: Boolean)
    def andIfNot(message: String): List[ConfigurationError] =
      if !bool then List(ConfigurationError.WRONG_PARAMETER(message)) else List.empty

  /** @param path
    *   The path of the file.
    * @return
    *   The content of the file as a String.
    */
  def readFile(path: String): Task[String]

  /** @param program
    *   The configuration file.
    * @param engine
    *   The Engine used to evaluate the configuration.
    * @return
    *   an Option of the configuration if there are no errors, or a None otherwise.
    */
  def reflectConfiguration(program: String)(using engine: ScriptEngine): Task[Option[Configuration]]

  /** @param configuration
    *   The simulation parameters of the configuration.
    * @return
    *   the result of configuration errors checking.
    */
  def checkErrors(configuration: Configuration): Task[ConfigurationResult]

object ConfigurationParser:
  def apply(): ConfigurationParser = new ConfigurationParserImpl()
  private class ConfigurationParserImpl() extends ConfigurationParser:

    override def readFile(path: String): Task[String] =
      Task(GlobalDefaults.DSL_IMPORTS + Source.fromFile(path).getLines().mkString)

    override def reflectConfiguration(program: String)(using engine: ScriptEngine): Task[Option[Configuration]] =
      try Task(Some(engine.eval(program).asInstanceOf[VirsimConfiguration]))
      catch case ex: Exception => Task(None)

    override def checkErrors(configuration: Configuration): Task[ConfigurationResult] =
      val errors: List[ConfigurationError] = List() :::
        (configuration.simulation.gridSide shouldBeWithin (MIN_VALUES.MIN_GRID_SIZE, MAX_VALUES.MAX_GRID_SIZE) andIfNot "Error: invalid parameter gridSide!") :::
        (configuration.simulation.numberOfEntities shouldBeWithin (MIN_VALUES.MIN_NUMBER_OF_ENTITIES, MAX_VALUES.MAX_NUMBER_OF_ENTITIES) andIfNot "Error: invalid parameter numberOfEntities!")
      errors.size match
        case 0 => Task(ConfigurationResult.OK(configuration))
        case _ => Task(ConfigurationResult.ERROR(errors))

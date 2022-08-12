package it.unibo.pps.control.loader

import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{
  Configuration,
  ConfigurationResult,
  VirsimConfiguration
}
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.virus.VirusComponent
import it.unibo.pps.entity.entity.Entities.*
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.given
import it.unibo.pps.entity.structure.Structures.House
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.loader.configuration.SimulationDefaults.StructuresDefault
import it.unibo.pps.control.loader.configuration.SimulationDefaults.VirusDefaults
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MAX_VALUES
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MIN_VALUES
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError.*
import it.unibo.pps.control.loader.configuration.ConfigurationParser
import it.unibo.pps.entity.common.Time.DurationTime
import it.unibo.pps.entity.entity.{EntityFactory, Infection}
import it.unibo.pps.entity.entity.Infection
import scala.concurrent.duration.DAYS
import scala.util.Random
import monocle.syntax.all.*
import monix.eval.Task

import javax.script.{ScriptEngine, ScriptEngineManager}
import scala.io.Source

object LoaderModule:

  trait Loader:
    /** @param configurationFile
      *   the configuration file with simulation parameters.
      * @return
      *   the result of the configuration parsing.
      */
    def parseConfiguration(configurationFile: String)(using parser: ConfigurationParser): Task[ConfigurationResult]

    /** @param configuration
      *   The configuration of the simulation, structures and virus.
      * @return
      *   the initialized environment.
      */
    def createEnvironment(configuration: Configuration)(using entityFactory: EntityFactory): Task[Environment]

    /** After parsing the configuration file and initializing the environment it starts the simulation engine.
      * @return
      *   the task responsible to init the engine and start the simulation.
      */
    def startEngine(configuration: Configuration): Task[Unit]

  trait Provider:
    val loader: Loader
  type Requirements = EngineModule.Provider with EnvironmentModule.Provider

  trait Component:
    context: Requirements =>

    class LoaderImpl extends Loader:

      override def parseConfiguration(filePath: String)(using parser: ConfigurationParser): Task[ConfigurationResult] =
        for
          program <- parser.readFile(filePath)
          configuration <- parser.reflectConfiguration(program)
          parsingResult <- configuration match
            case None =>
              Task(
                ConfigurationResult.ERROR(
                  List(ConfigurationError.INVALID_FILE("Invalid Scala file! Please check our DSL documentation !"))
                )
              )
            case Some(configuration: Configuration) => parser.checkErrors(configuration)
        yield parsingResult

      override def createEnvironment(configuration: Configuration)(using factory: EntityFactory): Task[Environment] =
        for
          entities <- factory.create(configuration)
          virus = configuration.virusConfiguration
          structures = configuration.structuresConfiguration
          environmentDuration = DurationTime(configuration.simulation.duration, DAYS)
        yield context.env.update(
          gridSide = configuration.simulation.gridSide,
          entities = entities,
          virus = virus,
          structures = structures,
          environmentDuration = environmentDuration
        )

      override def startEngine(configuration: Configuration): Task[Unit] =
        for
          initializedEnvironment <- createEnvironment(configuration)
          _ <- context.engine.startSimulationLoop(initializedEnvironment)
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>

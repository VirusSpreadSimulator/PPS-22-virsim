package it.unibo.pps.control.loader

import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{
  Configuration,
  ConfigurationResult,
  VirsimConfiguration
}
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.given
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.simulation
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.virus
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.structures
import it.unibo.pps.entity.common.GaussianProperty
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.virus.VirusComponent
import it.unibo.pps.entity.entity.Entities.*
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.House
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MAX_VALUES
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MIN_VALUES
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError.*
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
    def parseConfiguration(configurationFile: String): Task[ConfigurationResult]

    /** @param configuration
      *   The configuration of the simulation, structures and virus.
      * @return
      *   the initialized environment.
      */
    def createEnvironment(configuration: Configuration): Environment

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

      private def readFile(path: String): Task[String] =
        Task(GlobalDefaults.DSL_IMPORTS + Source.fromFile(path).getLines().mkString)

      private def reflectConfiguration(program: String)(using engine: ScriptEngine): Task[Option[Configuration]] =
        val canBeReflected = engine.eval(program).isInstanceOf[VirsimConfiguration]
        if canBeReflected then Task(Some(engine.eval(program).asInstanceOf[VirsimConfiguration])) else Task(None)

      private def checkErrors(configuration: Configuration): Task[ConfigurationResult] =
        Task(ConfigurationResult.OK(configuration))

      override def parseConfiguration(configurationFile: String): Task[ConfigurationResult] =
        for
          program <- readFile(configurationFile)
          configuration <- reflectConfiguration(program)
          parsingResult <- configuration match
            case None =>
              Task(
                ConfigurationResult.ERROR(
                  ConfigurationError.INVALID_FILE("Invalid Scala file! Please check our DSL documentation !")
                )
              )
            case Some(configuration: Configuration) => checkErrors(configuration)
        yield parsingResult

      override def createEnvironment(configuration: Configuration): Environment =
        // multiply grid side x3
        val entities: Set[SimulationEntity] = Set()
        val houses: Set[House] = Set()
        (configuration.simulation.numberOfEntities / configuration.simulation.peoplePerHouse)
        for
          i <- 0 until configuration.simulation.numberOfEntities
          entity = BaseEntity(
            i,
            GaussianIntDistribution(
              configuration.simulation.averagePopulationAge,
              configuration.simulation.stdDevPopulationAge
            ).next(),
            House(
              position = Point2D(1, 1),
              infectionProbability = 10,
              capacity = configuration.simulation.peoplePerHouse
            ),
            position = Point2D(1, 1)
          )
        yield entities + entity
        context.env.initialized(configuration.simulation.gridSide, entities, virus, structures)

      override def startEngine(configuration: Configuration): Task[Unit] =
        for
          initializedEnvironment <- Task(createEnvironment(configuration))
          _ <- Task(context.engine.init(configuration.simulation.duration))
          _ <- context.engine.startSimulationLoop(initializedEnvironment)
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>

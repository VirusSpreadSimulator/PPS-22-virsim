package it.unibo.pps.control.loader

import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{
  Configuration,
  ConfigurationResult,
  VirsimConfiguration
}
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
import it.unibo.pps.entity.entity.EntityComponent.Moving.movementGoal
import it.unibo.pps.entity.entity.IdGenerator.Generator
import it.unibo.pps.entity.entity.IdGenerator.IntegerIdGenerator
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.House
import monocle.syntax.all.*
import monix.eval.Task

import java.io.Reader
import javax.script.ScriptEngineManager
import scala.io.Source
import scala.quoted.*

object LoaderModule:
  trait Loader:

    /** @param configurationFile
      *   the configuration file with simulation parameters.
      * @return
      *   the result of the configuration parsing.
      */
    def parseConfiguration(configurationFile: String): ConfigurationResult

    /** @param configuration
      *   The configuration of the simulation, structures and virus.
      * @return
      *   the initialized environment.
      */
    def createEnvironment(configuration: Configuration): Unit

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
      override def parseConfiguration(configurationFile: String): ConfigurationResult =
        val configurationString: String = Source.fromFile(configurationFile).getLines().mkString
        val configuration: VirsimConfiguration = VirsimConfiguration(simulation, virus, structures)
        ConfigurationResult.OK(configuration)

      override def createEnvironment(configuration: Configuration): Unit =
        val idGenerator: Generator[Int] = IntegerIdGenerator(0)
        val entities: Set[Entity] = Set()
        for
          i <- 0 until configuration.simulation.numberOfEntities
          entity = SimulationEntity(
            idGenerator.next(),
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
        context.env.initializeEnvironment(entities, virus, structures)

      override def startEngine(configuration: Configuration): Task[Unit] =
        for
          _ <- Task(createEnvironment(configuration))
          _ <- Task(
            context.engine.init(configuration.simulation.duration, configuration.simulation.gridSide)
          )
          _ <- context.engine.startSimulationLoop()
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>

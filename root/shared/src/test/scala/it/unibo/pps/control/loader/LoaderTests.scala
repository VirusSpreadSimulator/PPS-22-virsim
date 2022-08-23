package it.unibo.pps.control.loader

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.boundary.component.Events
import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration}
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.virus
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.structures
import it.unibo.pps.entity.entity.EntityFactory
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.control.engine.config.EngineConfiguration.given_Scheduler
import it.unibo.pps.control.loader.LoaderTests.FakeConfigBoundary
import it.unibo.pps.control.loader.configuration.ConfigurationComponent
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.given
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.simulation
import it.unibo.pps.control.parser.ParserModule
import it.unibo.pps.control.parser.ReaderModule.{FilePath, StringFilePath}
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.Structures.{House, SimulationStructure}
import weaver.monixcompat.*
import monix.eval.Task
import monix.reactive.Observable

import java.nio.file.Path
import scala.concurrent.Promise

object LoaderTests extends SimpleTaskSuite with Matchers:

  class FakeConfigBoundary extends ConfigBoundary:
    override def init(): Task[Unit] = Task.pure {}
    override def start(): Task[Unit] = Task.pure {}
    override def stop(): Task[Unit] = Task.pure {}
    override def consume(env: EnvironmentModule.Environment): Task[Unit] = Task.pure {}
    override def events(): Observable[Events.Event] = Observable.empty
    override def config(): Task[FilePath] = Task.pure(StringFilePath(""))
    override def error(error: Seq[ConfigurationComponent.ConfigurationError]): Task[Unit] = Task.pure {}

  class FakeParser extends ParserModule.Parser:
    override def loadConfiguration(program: String): Task[Option[Configuration]] = Task(None)
    override def readFile(path: FilePath): Task[String] = Task("")
    override def checkErrors(configuration: Configuration): Task[ConfigurationComponent.ConfigurationResult] = Task(
      ConfigurationComponent.ConfigurationResult.OK(VirsimConfiguration(simulation, virus, structures))
    )

  object FakeLoader
      extends LoaderModule.Interface
      with BoundaryModule.Interface
      with EngineModule.Interface
      with ParserModule.Interface
      with EnvironmentModule.Interface:
    override val configBoundary: BoundaryModule.ConfigBoundary = FakeConfigBoundary()
    override val boundaries: Seq[BoundaryModule.Boundary] = Seq(configBoundary)
    override val loader: LoaderModule.Loader = LoaderImpl()
    override val env: EnvironmentModule.Environment = Environment.empty
    override val engine: EngineModule.Engine = EngineImpl()
    override val parser: ParserModule.Parser = FakeParser()

  val loader: LoaderModule.Loader = FakeLoader.loader

  test("Loader should be able to create the set of entities from the configuration") {
    val entityFactory: EntityFactory = EntityFactory()
    val entities: Int = 200
    val house: SimulationStructure = House(Point2D(10, 10), 0.8, entities)
    val simulation = Simulation(numberOfEntities = entities)
    for result <- entityFactory.create(VirsimConfiguration(simulation, virus, structures), Seq(house))
    yield expect(result.size == entities)
  }

  test("Loader should be able to create and initialize the whole environment") {
    for environment <- loader.createEnvironment(VirsimConfiguration(simulation, virus, structures))
    yield expect(environment.allEntities.nonEmpty)
  }

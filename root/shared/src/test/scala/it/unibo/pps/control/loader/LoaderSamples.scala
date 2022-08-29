package it.unibo.pps.control.loader

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.boundary.component.Events
import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.simulation
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.structures
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.virus
import it.unibo.pps.control.loader.extractor.EntitiesStats.{Alive, Deaths, Healthy, Infected, Sick}
import it.unibo.pps.control.loader.extractor.EnvironmentStats.{Days, Hours}
import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.control.loader.extractor.HospitalStats.HospitalPressure
import it.unibo.pps.control.parser.{ParserModule, ReaderModule}
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.control.parser.ReaderModule.{FilePath, StringFilePath}
import it.unibo.pps.entity.environment.EnvironmentModule
import monix.eval.Task
import monix.reactive.Observable
import org.virtuslab.yaml.internal.load.parse.ParserImpl

import scala.io.Source

object LoaderSamples:
  class TestConfigBoundary extends ConfigBoundary:
    override def init(): Task[Unit] = Task.pure {}
    override def start(): Task[Unit] = Task.pure {}
    override def stop(): Task[Unit] = Task.pure {}
    override def consume(env: EnvironmentModule.Environment): Task[Unit] = Task.pure {}
    override def events(): Observable[Events.Event] = Observable.empty
    override def config(): Task[FilePath] = Task.pure(StringFilePath(""))
    override def error(error: Seq[ConfigurationComponent.ConfigurationError]): Task[Unit] = Task.pure {}

  class TestParser extends ParserModule.Parser:
    override def loadConfiguration(program: String): Task[Option[Configuration]] = Task(None)
    override def readFile(path: FilePath): Task[String] = Task("")
    override def checkErrors(configuration: Configuration): Task[ConfigurationComponent.ConfigurationResult] = Task(
      ConfigurationComponent.ConfigurationResult.OK(VirsimConfiguration(simulation, virus, structures))
    )

  object TestLoader
      extends LoaderModule.Interface
      with BoundaryModule.Interface
      with EngineModule.Interface
      with ParserModule.Interface
      with EnvironmentModule.Interface:
    override val configBoundary: BoundaryModule.ConfigBoundary = TestConfigBoundary()
    override val boundaries: Seq[BoundaryModule.Boundary] = Seq(configBoundary)
    override val loader: LoaderImpl = LoaderImpl()
    override val env: EnvironmentModule.Environment = Environment.empty
    override val engine: EngineModule.Engine = EngineImpl()
    override val parser: ParserModule.Parser = TestParser()

  class MockReader extends ReaderModule.Reader:
    override def read(filePath: ReaderModule.FilePath): Task[String] =
      for
        source <- Task(Source.fromFile(filePath.asInstanceOf[StringFilePath].path))
        fileContent <- Task(source.mkString)
        _ <- Task(source.close())
      yield fileContent

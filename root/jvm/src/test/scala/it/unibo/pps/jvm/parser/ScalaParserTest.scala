package it.unibo.pps.jvm.parser

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{ConfigurationResult, VirsimConfiguration}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MAX_VALUES
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.control.parser.ReaderModule.StringFilePath
import it.unibo.pps.control.parser.{ParserModule, ReaderModule}
import it.unibo.pps.entity.Samples.{house, permanence}
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Time.DurationTime
import it.unibo.pps.entity.structure.Structures.GenericBuilding
import it.unibo.pps.jvm.parser.{JVMReader, ScalaParser}
import monix.eval.Task
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.SimpleTaskSuite

import scala.concurrent.duration.{DAYS, MINUTES}

object ScalaParserTest extends SimpleTaskSuite with Matchers:

  object TestScalaParser extends ScalaParser.Interface with ReaderModule.Interface with JVMReader.Interface:
    override val scalaParser: Parser = ParserImpl()
    override val jvmReader: ReaderModule.Reader = JVMReaderImpl()
    override val reader: ReaderModule.Reader = jvmReader

  private val scalaParser: Parser = TestScalaParser.scalaParser

  test("ScalaParser should be able to read the configuration file with JVMReader") {
    for configFile <- scalaParser.readFile(StringFilePath(getClass.getResource("/configuration.scala").getFile))
    yield expect(configFile.nonEmpty)
  }

  test("ScalaParser should be able to detect wrong grid side parameter") {
    val wrongSimulation = simulation gridSide MAX_VALUES.MAX_GRID_SIZE + 1
    for
      configuration <- Task(VirsimConfiguration(wrongSimulation, virus, structures))
      configResult <- scalaParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

  test("ScalaParser should be able to detect wrong number of entities") {
    val wrongSimulation = simulation entities 10000
    for
      configuration <- Task(VirsimConfiguration(wrongSimulation, virus, structures))
      configResult <- scalaParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

  test("ScalaParser should be able to detect wrong virus spread rate parameter") {
    val wrongVirus = virus spreadRate 10
    for
      configuration <- Task(VirsimConfiguration(simulation, wrongVirus, structures))
      configResult <- scalaParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

  test("ScalaParser should be able to detect errors in structures positions") {
    val wrongStructures =
      structures + GenericBuilding(Point2D(MAX_VALUES.MAX_GRID_SIZE + 1, MAX_VALUES.MAX_GRID_SIZE + 1), 0.5, 4)
    for
      configuration <- Task(VirsimConfiguration(simulation, virus, wrongStructures))
      configResult <- scalaParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

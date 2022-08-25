package it.unibo.pps.control.parser

import it.unibo.pps.control.loader.configuration.SimulationDefaults.{GlobalDefaults, MAX_VALUES}
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.control.parser.ReaderModule.StringFilePath
import it.unibo.pps.control.loader.LoaderSamples.MockReader
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.VirsimConfiguration
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.*
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.GenericBuilding
import monix.eval.Task
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.SimpleTaskSuite

import scala.io.Source

object YAMLParserTest extends SimpleTaskSuite with Matchers:

  object TestYAMLParser extends YAMLParser.Interface with ReaderModule.Interface:
    override val YAMLParser: Parser = ParserImpl()
    override val readers: Seq[ReaderModule.Reader] = Seq(MockReader())

  private val yamlParser: Parser = TestYAMLParser.YAMLParser

  test("YamlParser should be able to read the configuration file") {
    for configFile <- yamlParser.readFile(StringFilePath(getClass.getResource("/configuration.yml").getFile))
    yield expect(configFile.nonEmpty)
  }

  test("YamlParser should be able to detect wrong grid side parameter") {
    val wrongSimulation = simulation gridSide 100
    for
      configuration <- Task(VirsimConfiguration(wrongSimulation, virus, structures))
      configResult <- yamlParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

  test("YamlParser should be able to detect wrong number of entities") {
    val wrongSimulation = simulation entities 10000
    for
      configuration <- Task(VirsimConfiguration(wrongSimulation, virus, structures))
      configResult <- yamlParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

  test("YamlParser should be able to detect wrong virus spread rate parameter") {
    val wrongVirus = virus spreadRate 10
    for
      configuration <- Task(VirsimConfiguration(simulation, wrongVirus, structures))
      configResult <- yamlParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

  test("YamlParser should be able to detect errors in structures positions") {
    val wrongStructures =
      structures + GenericBuilding(Point2D(MAX_VALUES.MAX_GRID_SIZE + 1, MAX_VALUES.MAX_GRID_SIZE + 1), 0.5, 4)
    for
      configuration <- Task(VirsimConfiguration(simulation, virus, wrongStructures))
      configResult <- yamlParser.checkErrors(configuration)
    yield expect(configResult match
      case ConfigurationResult.ERROR(_) => true
      case ConfigurationResult.OK(_) => false
    )
  }

  test("yamlParser should be able to load a configuration") {
    for
      configFile <- yamlParser.readFile(StringFilePath(getClass.getResource("/configuration.yml").getFile))
      configResult <- yamlParser.loadConfiguration(configFile)
    yield expect(configResult match
      case Some(_) => true
      case None => false
    )
  }

package it.unibo.pps.jvm.control.parser

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.VirsimConfiguration
import it.unibo.pps.control.parser.{ParserModule, ReaderModule}
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.jvm.parser.{JVMReader, ScalaParser}
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationResult
import it.unibo.pps.control.parser.ReaderModule.StringFilePath
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.SimpleTaskSuite
import monix.eval.Task

object ScalaParserTest extends SimpleTaskSuite with Matchers:

  object MockScalaParser extends ScalaParser.Interface with ReaderModule.Interface with JVMReader.Interface:
    override val scalaParser: Parser = ParserImpl()
    override val jvmReader: ReaderModule.Reader = JVMReaderImpl()
    override val reader: ReaderModule.Reader = jvmReader

  val scalaParser: Parser = MockScalaParser.scalaParser

  test("ScalaParser should be able to read the configuration file") {
    for configFile <- scalaParser.readFile(StringFilePath(getClass.getResource("/configuration.scala").getFile))
    yield expect(configFile.nonEmpty)
  }

  test("ScalaParser should be able to detect simulation parameters errors") {
    val wrongSimulation = simulation entities 10000
    for
      configuration <- Task(VirsimConfiguration(wrongSimulation, virus, structures))
      result <- scalaParser.checkErrors(configuration)
    yield expect(result.isInstanceOf[ConfigurationResult.ERROR])
  }

  test("ScalaParser should be able to detect virus parameters errors") {
    val wrongVirus = virus spreadRate 10
    for
      configuration <- Task(VirsimConfiguration(simulation, wrongVirus, structures))
      result <- scalaParser.checkErrors(configuration)
    yield expect(result.isInstanceOf[ConfigurationResult.ERROR])
  }

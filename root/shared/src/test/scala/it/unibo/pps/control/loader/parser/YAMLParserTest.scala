package it.unibo.pps.control.loader.parser

import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.control.parser.YAMLParser.*
import it.unibo.pps.control.parser.{ParserModule, YAMLParser}
import monix.eval.Task
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.SimpleTaskSuite

object YAMLParserTest extends SimpleTaskSuite with Matchers:

  object FakeYAMLParser extends YAMLParser.Interface:
    override val YAMLParser: ParserModule.Parser = ParserImpl()

  val yamlParser: Parser = FakeYAMLParser.YAMLParser

  test("YAMLParser should be able to read a YAML configuration file") {
    for yamlFile <- yamlParser.readFile(getClass.getResource("/configuration.yml").getFile)
    yield expect(yamlFile.nonEmpty)
  }

  test("YAMLParser should be able to parse the YAML configuration file") {
    for
      yamlFile <- yamlParser.readFile(getClass.getResource("/configuration.yml").getFile)
      configuration <- yamlParser.loadConfiguration(yamlFile)
    yield expect(configuration.nonEmpty)
  }

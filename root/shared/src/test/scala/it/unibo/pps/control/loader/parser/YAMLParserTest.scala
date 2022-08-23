package it.unibo.pps.control.loader.parser

import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.control.parser.ReaderModule.StringFilePath
import it.unibo.pps.control.parser.YAMLParser.*
import it.unibo.pps.control.parser.{ParserModule, ReaderModule, YAMLParser}
import monix.eval.Task
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.SimpleTaskSuite

object YAMLParserTest extends SimpleTaskSuite with Matchers:

  class FakeReader extends ReaderModule.Reader:
    override def read(filePath: ReaderModule.FilePath): Task[String] = Task("")

  object FakeYAMLParser extends YAMLParser.Interface with ReaderModule.Interface:
    override val YAMLParser: ParserModule.Parser = ParserImpl()
    override val reader: ReaderModule.Reader = FakeReader()

  val yamlParser: Parser = FakeYAMLParser.YAMLParser

//  test("YAMLParser should be able to read a YAML configuration file") {
//    for yamlFile <- yamlParser.readFile(StringFilePath(getClass.getResource("/configuration.yml").getFile))
//    yield expect(yamlFile.nonEmpty)
//  }
//
//  test("YAMLParser should be able to parse the YAML configuration file") {
//    for
//      yamlFile <- yamlParser.readFile(StringFilePath(getClass.getResource("/configuration.yml").getFile))
//      configuration <- yamlParser.loadConfiguration(yamlFile)
//    yield expect(configuration.nonEmpty)
//  }

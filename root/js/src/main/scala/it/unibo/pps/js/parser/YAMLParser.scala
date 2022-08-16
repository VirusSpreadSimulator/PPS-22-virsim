package it.unibo.pps.js.parser

import it.unibo.pps.control.loader.configuration.ConfigurationComponent
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.Configuration
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationResult
import it.unibo.pps.control.parser.ParserModule.Parser
import monix.eval.Task

object YAMLParser:

  trait Provider:
    val YAMLParser: Parser

  trait Component:

    class ParserImpl extends Parser:
      override def readFile(path: String): Task[String] = ???

      override def loadConfiguration(program: String): Task[Option[Configuration]] = ???

  trait Interface extends Provider with Component

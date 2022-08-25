package it.unibo.pps.jvm.parser

import it.unibo.pps.control.loader.LoaderModule.Requirements
import it.unibo.pps.control.loader.configuration.ConfigurationComponent
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{
  Configuration,
  ConfigurationResult,
  VirsimConfiguration
}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.control.parser.ReaderModule
import it.unibo.pps.control.parser.ReaderModule.FilePath
import it.unibo.pps.jvm.boundary.gui.GUIModule.{Component, Provider}
import monix.eval.Task

import javax.script.ScriptEngineManager
import scala.io.Source

/** The Configuration parser for Scala files. It uses Java Reflection to instantiate the configuration written with the
  * DSL in the system.
  */
object ScalaParser:

  trait Provider:
    val scalaParser: Parser

  type Requirements = ReaderModule.Provider

  trait Component:
    context: Requirements =>
    class ParserImpl extends Parser:

      override def readFile(filePath: FilePath): Task[String] = context.readers.head.read(filePath)

      override def loadConfiguration(program: String): Task[Option[Configuration]] =
        for
          engine <- Task(new ScriptEngineManager().getEngineByName("scala"))
          configuration <- Task {
            engine.eval(program) match
              case configuration: VirsimConfiguration => Some(configuration)
              case _ => None
          }
        yield configuration

  trait Interface extends Provider with Component:
    self: Requirements =>

package it.unibo.pps.jvm.parser

import it.unibo.pps.control.loader.configuration.ConfigurationComponent
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{
  Configuration,
  ConfigurationResult,
  VirsimConfiguration
}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.jvm.boundary.GUIModule.{Component, Provider}
import monix.eval.Task

import javax.script.ScriptEngineManager
import scala.io.Source

/** The Configuration parser for Scala files. It uses Java Reflection to instantiate the configuration written with the
  * DSL in the system.
  */
object ScalaParser:

  trait Provider:
    val scalaParser: Parser
  trait Component:
    class ParserImpl extends Parser:

      override def readFile(path: String): Task[String] =
        for
          source <- Task(Source.fromFile(path))
          fileContent <- Task(GlobalDefaults.DSL_IMPORTS + source.mkString)
          _ <- Task(source.close())
        yield fileContent

      override def loadConfiguration(program: String): Task[Option[Configuration]] =
        for
          engine <- Task(new ScriptEngineManager().getEngineByName("scala"))
          configuration <- Task {
            engine.eval(program) match
              case configuration: VirsimConfiguration => Some(configuration)
              case _ => None
          }
        yield configuration

  trait Interface extends Provider with Component

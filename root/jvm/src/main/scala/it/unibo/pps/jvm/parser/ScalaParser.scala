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

object ScalaParser:

  trait Provider:
    val scalaParser: Parser
  trait Component:
    class ParserImpl extends Parser:
      override def readFile(path: String): Task[String] =
        Task(GlobalDefaults.DSL_IMPORTS + Source.fromFile(path).getLines().mkString)

      override def loadConfiguration(program: String): Task[Option[Configuration]] =
        val engine = new ScriptEngineManager().getEngineByName("scala")
        try Task(Some(engine.eval(program).asInstanceOf[VirsimConfiguration]))
        catch case ex: Exception => Task(None)

  trait Interface extends Provider with Component

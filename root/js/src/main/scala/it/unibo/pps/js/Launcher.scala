package it.unibo.pps.js

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.launcher.Launch
import it.unibo.pps.control.parser.{ParserModule, YAMLParser}
import it.unibo.pps.js.boundary.JSGUIModule

object Launcher extends Launch with JSGUIModule.Interface with YAMLParser.Interface:
  override val jsGui = JSGUIBoundaryImpl()
  override val configBoundary = jsGui
  override val boundaries = Seq(jsGui)
  override val YAMLParser: ParserModule.Parser = ParserImpl()
  override val parser: ParserModule.Parser = YAMLParser

  @main def main(): Unit = launch()

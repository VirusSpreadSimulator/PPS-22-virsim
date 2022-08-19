package it.unibo.pps.jvm

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.control.launcher.Launch
import it.unibo.pps.control.parser.ParserModule
import it.unibo.pps.jvm.boundary.GUIModule
import it.unibo.pps.jvm.parser.ScalaParser

object Launcher extends Launch with GUIModule.Interface with ScalaParser.Interface:

  override val gui: ConfigBoundary = GUIBoundaryImpl()
  override val configBoundary: ConfigBoundary = gui
  override val boundaries: Seq[BoundaryModule.Boundary] = Seq(gui, exporter)
  override val scalaParser: ParserModule.Parser = ParserImpl()
  override val parser: ParserModule.Parser = scalaParser

  @main def main(): Unit = launch()

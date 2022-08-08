package it.unibo.pps.jvm

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.control.launcher.Launch
import it.unibo.pps.jvm.boundary.GUIModule

object Launcher extends Launch with GUIModule.Interface:

  override val gui = GUIBoundaryImpl()
  override val configBoundary: ConfigBoundary = gui
  override val boundaries = Seq(gui)

  @main def main(): Unit = launch()

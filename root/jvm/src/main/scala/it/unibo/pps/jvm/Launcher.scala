package it.unibo.pps.jvm

import it.unibo.pps.jvm.boundary.GUIModule
import it.unibo.pps.control.Launch

object Launcher extends Launch with GUIModule.Interface:

  override val gui = GUIBoundaryImpl()
  override val boundaries = Seq(gui)

  @main def main(): Unit = launch()

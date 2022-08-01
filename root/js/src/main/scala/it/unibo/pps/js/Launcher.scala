package it.unibo.pps.js

import it.unibo.pps.control.launcher.Launch
import it.unibo.pps.js.boundary.JSGUIModule

object Launcher extends Launch with JSGUIModule.Interface:
  override val jsGui = JSGUIBoundaryImpl()
  override val boundaries = Seq(jsGui)

  @main def main(): Unit = launch()

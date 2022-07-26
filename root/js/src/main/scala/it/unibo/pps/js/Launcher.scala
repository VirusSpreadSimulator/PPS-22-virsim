package it.unibo.pps.js

import it.unibo.pps.js.boundary.JSGUIModule
import it.unibo.pps.control.Launch

object Launcher extends Launch with JSGUIModule.Interface:
  override val jsGui = JSGUIBoundaryImpl()
  override val boundaries = Seq(jsGui)

  @main def main(): Unit = launch()

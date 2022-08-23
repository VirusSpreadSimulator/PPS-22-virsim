package it.unibo.pps.js

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.BoundaryModule.Boundary
import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.js.boundary.parser.JSReader
import it.unibo.pps.control.launcher.Launch
import it.unibo.pps.control.parser.{ParserModule, ReaderModule, YAMLParser}
import it.unibo.pps.js.boundary.JSGUIModule

object Launcher extends Launch with JSGUIModule.Interface with YAMLParser.Interface with JSReader.Interface:
  override val jsGui: ConfigBoundary = JSGUIBoundaryImpl()
  override val configBoundary: ConfigBoundary = jsGui
  override val boundaries: Seq[Boundary] = Seq(jsGui)
  override val YAMLParser: ParserModule.Parser = ParserImpl()
  override val parser: ParserModule.Parser = YAMLParser
  override val jsReader: ReaderModule.Reader = JSReaderImpl()
  override val reader: ReaderModule.Reader = jsReader

  @main def main(): Unit = launch()

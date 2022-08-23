package it.unibo.pps.js.parser

import it.unibo.pps.control.parser.ReaderModule.FilePath
import org.scalajs.dom

case class JsFilePath(path: dom.File) extends FilePath:
  override type Path = dom.File
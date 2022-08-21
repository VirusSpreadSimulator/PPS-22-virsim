package it.unibo.pps.js.boundary

import it.unibo.pps.boundary.FilePath
import org.scalajs.dom

case class JsFilePath(path: dom.File) extends FilePath:
  override type Path = dom.File

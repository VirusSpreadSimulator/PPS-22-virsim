package it.unibo.pps.boundary

case class StringFilePath(path: String) extends FilePath:
  override type Path = String

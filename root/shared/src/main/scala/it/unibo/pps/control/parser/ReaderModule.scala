package it.unibo.pps.control.parser

import monix.eval.Task

object ReaderModule:

  /** Interface that defines the configuration file reader. All the readers need to extend this trait in order to be
    * compatible. Every module (jvm, js) has to implement its own configuration reader.
    */
  trait Reader:
    /** @param path
      *   The path of the file.
      * @return
      *   The content of the file as a String.
      */
    def read(filePath: FilePath): Task[String]

  trait FilePath:
    type Path
    def path: Path

  case class StringFilePath(path: String) extends FilePath:
    override type Path = String

  /** Provider of the component */
  trait Provider:
    /** The current parser of the configuration */
    val reader: Reader

  trait Interface extends Provider

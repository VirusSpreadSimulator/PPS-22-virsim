package it.unibo.pps.js.parser

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.parser.ReaderModule
import it.unibo.pps.control.parser.ReaderModule.{FilePath, Reader}
import org.scalajs.dom.{File, FileReader, UIEvent}
import monix.eval.Task
import monix.reactive.Consumer
import monix.reactive.subjects.PublishSubject
import org.scalajs.dom

/** The reader of files for the JS module. */
object JSReader:

  trait Provider:
    val jsReader: Reader

  trait Component:
    /** Return the content of the configuration file inserted by the user. */
    class JSReaderImpl extends Reader:

      private val filePS: PublishSubject[String] = PublishSubject[String]()

      override def read(filePath: FilePath): Task[String] =
        val reader: FileReader = new FileReader()
        val file: File = filePath.path.asInstanceOf[File]
        reader.readAsText(file, "UTF-8")
        reader.onload = _ => filePS.onNext(s"${reader.result}")
        Task.defer(filePS.consumeWith(Consumer.head))

  /** The path of the JS module consists in the file inserted by the user.
    * @param path
    *   the file in input by the user
    */
  case class JSFilePath(path: File) extends FilePath:
    override type Path = File

  trait Interface extends Provider with Component

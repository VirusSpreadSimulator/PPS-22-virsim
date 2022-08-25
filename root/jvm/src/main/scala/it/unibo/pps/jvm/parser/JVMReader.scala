package it.unibo.pps.jvm.parser

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.parser.ReaderModule.StringFilePath
import it.unibo.pps.control.parser.ReaderModule.{FilePath, Reader}

import javax.script.ScriptEngineManager
import scala.io.Source
import monix.eval.Task

/** The Reader of files for the JVM module. */
object JVMReader:

  trait Provider:
    val jvmReader: Reader

  trait Component:
    /** It accepts a Path and then read the file using Java-based API. */
    class JVMReaderImpl extends Reader:

      override def read(filePath: FilePath): Task[String] =
        for
          source <- Task(Source.fromFile(filePath.asInstanceOf[StringFilePath].path))
          fileContent <- Task(GlobalDefaults.DSL_IMPORTS + source.mkString)
          _ <- Task(source.close())
        yield fileContent

  trait Interface extends Provider with Component

package it.unibo.pps.jvm.parser

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.parser.ReaderModule
import it.unibo.pps.control.parser.ReaderModule.StringFilePath
import it.unibo.pps.jvm.parser.JVMReader
import monix.eval.Task
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.SimpleTaskSuite

object JVMReaderTest extends SimpleTaskSuite with Matchers:

  object TestJVMReader extends JVMReader.Interface:
    override val jvmReader: ReaderModule.Reader = JVMReaderImpl()

  private val reader = TestJVMReader.jvmReader

  test("JVMReader should be able to read a file") {
    for {
      content <- reader.read(StringFilePath(getClass.getResource("/configuration.scala").getPath))
    } yield expect(content.contains("VirsimConfiguration"))
  }

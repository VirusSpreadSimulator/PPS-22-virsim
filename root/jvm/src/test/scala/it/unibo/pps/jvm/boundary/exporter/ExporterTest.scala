package it.unibo.pps.jvm.boundary.exporter

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.parser.ReaderModule
import it.unibo.pps.control.parser.ReaderModule.StringFilePath
import it.unibo.pps.jvm.parser.JVMReader
import it.unibo.pps.jvm.boundary.gui.Values.Text
import it.unibo.pps.entity.Samples
import monix.eval.Task
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.SimpleTaskSuite

import scala.io.Source

object ExporterTest extends SimpleTaskSuite with Matchers:

  object TestExporter extends ExporterModule.Interface:
    override val exporter: BoundaryModule.Boundary with ExporterModule.Exporter = FileExporterImpl()

  private val env = Samples.sampleEnv

  test("Exporter should have a non-empty list of extractors") {
    val exporter = TestExporter.exporter
    for {
      _ <- Task.pure {}
      extractors = exporter.extractors
    } yield expect(extractors.nonEmpty)
  }

  test("Exporter should correctly consume and export the environment") {
    val exporter = TestExporter.exporter
    for {
      _ <- exporter.init()
      _ <- exporter.start()
      _ <- exporter.consume(env)
      _ <- exporter.stop()
      source = Source.fromFile(exporter.outputFile)
      fileContent = source.mkString
      _ <- Task(source.close())
    } yield expect(exporter.extractors.forall(ex => fileContent.contains(ex.name)))
  }

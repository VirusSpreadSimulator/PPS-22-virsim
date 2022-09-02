package it.unibo.pps.jvm.boundary.exporter

import it.unibo.pps.boundary.BoundaryModule.Boundary
import it.unibo.pps.boundary.component.Events
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.control.loader.extractor.StatisticalData.Stats
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.jvm.boundary.gui.Values.Text
import monix.eval.Task
import monix.reactive.Observable

import java.io.{File, FileWriter}
import java.nio.file.Files

/** The Exporter of the simulation. It's another boundary of the application so it receives the environment from the
  * engine at every simulation step.
  */
object ExporterModule:

  /** The provider of the exporter instance. */
  trait Provider:
    val exporter: Boundary

  trait Exporter:

    /** The path of the output file.
      * @return
      *   the path of the file.
      */
    def outputFile: String

    /** The data extractors used by the exporter to extract some statistics from the environment.
      * @return
      *   the list of extractors.
      */
    def extractors: List[DataExtractor[_]]

  trait Component:

    /** A File based implementation of the Exporter. It exports data on a CSV file under the temp directory of the user.
      * @param extractors
      *   the list of extractors to extract some statistics from the environment.
      */
    class FileExporterImpl(
        val extractors: List[DataExtractor[_]] = List(
          Stats.HOURS,
          Stats.DAYS,
          Stats.INFECTED,
          Stats.SICK,
          Stats.DEATHS,
          Stats.HOSPITAL_FREE_SEATS,
          Stats.HOSPITALIZED,
          Stats.HOSPITAL_PRESSURE
        )
    ) extends Boundary
        with Exporter:

      private val tempDir = Files.createTempDirectory(GlobalDefaults.EXPORT_DIR_NAME)
      private val fileWriter = new FileWriter(outputFile)

      override def outputFile: String = tempDir.toFile.getPath + '/' + GlobalDefaults.EXPORT_FILE_NAME

      override def init(): Task[Unit] =
        for _ <- Task(fileWriter.write(Text.EXPORT_INIT_TITLE))
        yield ()

      override def start(): Task[Unit] =
        for
          _ <- Task {
            fileWriter.write(extractors.foldLeft("")((columns, ex) => columns + ex.name + ",") + "\n")
          }
        yield ()

      override def consume(env: Environment): Task[Unit] =
        for
          isTimeToExport <- Task(env.time.relativeTicks % GlobalDefaults.EXPORT_INTERVAL == 0)
          _ <- Task {
            if isTimeToExport then
              fileWriter.write(
                extractors.foldLeft("")((data, ext) => data + ext.extractData(env).toString + ",") + "\n"
              )
          }
        yield ()

      override def events(): Observable[Events.Event] =
        Observable.empty

      override def stop(): Task[Unit] =
        for _ <- Task(fileWriter.close())
        yield ()

  trait Interface extends Provider with Component

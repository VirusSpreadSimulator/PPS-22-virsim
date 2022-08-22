package it.unibo.pps.jvm.boundary.exporter

import it.unibo.pps.boundary.BoundaryModule.Boundary
import it.unibo.pps.boundary.component.Events
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import Extractors.DataExtractor
import StatisticalData.Stats
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.common.Time.TimeStamp
import monix.eval.Task
import monix.reactive.Observable

import java.io.{File, FileWriter}
import java.nio.file.Files

object ExporterModule:

  trait Provider:
    val exporter: Boundary
  trait Component:
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
    ) extends Boundary:
      private val file: File = new File(GlobalDefaults.EXPORT_DIR_NAME)
      private val tempDir = Files.createTempDirectory(GlobalDefaults.EXPORT_DIR_NAME)
      private val fileWriter = new FileWriter(tempDir.toFile.getPath + '/' + GlobalDefaults.EXPORT_FILE_NAME)

      override def init(): Task[Unit] =
        for _ <- Task(fileWriter.write("VirSim Simulation Statistics\n\n"))
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

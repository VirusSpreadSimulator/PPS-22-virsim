package it.unibo.pps.boundary.exporter

import it.unibo.pps.boundary.BoundaryModule.Boundary
import it.unibo.pps.boundary.component.Events
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.boundary.exporter.Extractors.DataExtractor
import it.unibo.pps.boundary.exporter.StatisticalData.Stats
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.common.Time.TimeStamp
import monix.eval.Task
import monix.reactive.Observable

import java.io.{File, FileWriter}

object ExporterModule:

  trait Provider:
    val exporter: Boundary
  trait Component:
    class FileExporterImpl(
        val extractors: List[DataExtractor[_]] = List(
          Stats.DAYS,
          Stats.INFECTED,
          Stats.SICK,
          Stats.DEATHS,
          Stats.HOSPITAL_FREE_SEATS,
          Stats.HOSPITALIZED,
          Stats.HOSPITAL_PRESSURE
        )
    ) extends Boundary:
      private val file: File = new File(GlobalDefaults.EXPORT_FILE_PATH)
      private val fileWriter: FileWriter = new FileWriter(file)

      override def init(): Task[Unit] =
        for _ <- Task(fileWriter.write("VirSim Simulation Statistics\n\n"))
        yield ()

      override def start(): Task[Unit] =
        for
          _ <- Task {
            fileWriter.write(extractors.foldLeft("")((columns, ex) => columns + ex.name + "\t\t") + "\n")
          }
        yield ()

      override def consume(env: Environment): Task[Unit] =
        for
          isTimeToExport <- Task(env.time.relativeTicks % GlobalDefaults.EXPORT_INTERVAL == 0)
          _ <- Task {
            if isTimeToExport then
              fileWriter.write(
                extractors.foldLeft("")((data, ext) => data + ext.extractData(env).toString + "\t\t") + "\n"
              )
          }
        yield ()

      override def events(): Observable[Events.Event] =
        Observable.empty

      override def stop(): Task[Unit] =
        for _ <- Task(fileWriter.close())
        yield ()

  trait Interface extends Provider with Component

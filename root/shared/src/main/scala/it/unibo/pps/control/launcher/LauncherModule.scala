package it.unibo.pps.control.launcher

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.loader.LoaderModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationResult
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationResult.*

import monix.eval.Task

object LauncherModule:
  trait Launcher:
    def launch(): Task[Unit]
    def launcherLoop(): Task[Unit] // todo: is it useful here? I think that may be only private
  trait Provider:
    val launcher: Launcher
  type Requirements = BoundaryModule.Provider with LoaderModule.Provider
  trait Component:
    context: Requirements =>
    class LauncherImpl extends Launcher:

      override def launch(): Task[Unit] =
        for
          _ <- Task.sequence(context.boundaries.map(_.init()))
          _ <- launcherLoop()
        yield ()

      override def launcherLoop(): Task[Unit] =
        for
          path <- context.configBoundary.config()
          configResult <- context.loader.parseConfiguration("configuration.scala")
          _ <- configResult match
            case ERROR(errors) =>
              for
                _ <- context.configBoundary.error(errors)
                _ <- launcherLoop()
              yield ()
            case OK(configuration) =>
              for
                _ <- Task.sequence(context.boundaries.map(_.start()))
                _ <- context.loader.startEngine(configuration)
              yield ()
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>

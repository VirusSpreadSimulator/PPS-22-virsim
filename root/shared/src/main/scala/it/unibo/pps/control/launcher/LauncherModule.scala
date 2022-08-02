package it.unibo.pps.control.launcher

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.loader.LoaderModule
import monix.eval.Task

object LauncherModule:
  trait Launcher:
    def launch(): Task[Unit]
  trait Provider:
    val launcher: Launcher
  type Requirements = BoundaryModule.Provider with LoaderModule.Provider
  trait Component:
    context: Requirements =>
    class LauncherImpl extends Launcher:
      override def launch(): Task[Unit] =
        for
          _ <- Task.sequence(context.boundaries.map(_.init()))
          _ <- Task(context.loader.load("configuration.scala"))
        yield ()
  trait Interface extends Provider with Component:
    self: Requirements =>

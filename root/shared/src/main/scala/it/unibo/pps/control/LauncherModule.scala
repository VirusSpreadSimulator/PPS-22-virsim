package it.unibo.pps.control

import it.unibo.pps.boundary.BoundaryModule
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
          _ <- context.loader.load(10)
        yield ()
  trait Interface extends Provider with Component:
    self: Requirements =>

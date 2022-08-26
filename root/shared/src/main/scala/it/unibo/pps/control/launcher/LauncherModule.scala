package it.unibo.pps.control.launcher

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.loader.LoaderModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationResult.*
import monix.eval.Task

object LauncherModule:
  /** The launcher is the component that is responsible for the launch of the simulation. */
  trait Launcher:
    /** Method that function as entry point for all the flow.
      * @return
      *   the task
      */
    def launch(): Task[Unit]
  /** Provider of the component. */
  trait Provider:
    val launcher: Launcher
  /** The launcher require:
    *   - the loader in order to launch the loading of the user configuration and of the environment
    *   - the boundaries in order to be able to signal errors and the start of the simulation.
    */
  type Requirements = BoundaryModule.Provider with LoaderModule.Provider
  /** Launcher component. */
  trait Component:
    context: Requirements =>
    /** Implementation of the launcher component. */
    class LauncherImpl extends Launcher:

      override def launch(): Task[Unit] =
        for
          _ <- Task.sequence(context.boundaries.map(_.init()))
          _ <- launcherLoop()
        yield ()

      /** Method that is responsible to loop until the user submit a correct configuration that can be loaded. */
      private def launcherLoop(): Task[Unit] =
        for
          path <- context.configBoundary.config()
          configResult <- context.loader.parseConfiguration(path)
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
  /** The interface of the component. */
  trait Interface extends Provider with Component:
    self: Requirements =>

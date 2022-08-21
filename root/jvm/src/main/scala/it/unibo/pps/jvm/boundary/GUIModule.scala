package it.unibo.pps.jvm.boundary

import it.unibo.pps.boundary.BoundaryModule.{Boundary, ConfigBoundary}
import it.unibo.pps.boundary.{FilePath, StringFilePath}
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.gui.{InitGUI, SimulationGUI}
import monix.eval.Task
import monix.reactive.Observable

/** Config Boundary implementation for the JVM GUI. */
object GUIModule:
  trait Provider:
    val gui: ConfigBoundary
  trait Component:
    class GUIBoundaryImpl extends ConfigBoundary:
      private val initSimulationScreen: InitGUI = InitGUI()
      private val simulationScreen: SimulationGUI = SimulationGUI()
      override def init(): Task[Unit] = initSimulationScreen.init()
      override def config(): Task[FilePath] = initSimulationScreen.config().map(StringFilePath.apply)
      override def error(errors: Seq[ConfigurationError]): Task[Unit] = initSimulationScreen.error(errors)
      override def start(): Task[Unit] = initSimulationScreen.start(simulationScreen)
      override def stop(): Task[Unit] = simulationScreen.stop()
      override def consume(env: Environment): Task[Unit] = simulationScreen.render(env)
      override def events(): Observable[Event] = simulationScreen.events()
  trait Interface extends Provider with Component

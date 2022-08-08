package it.unibo.pps.jvm.boundary

import it.unibo.pps.boundary.BoundaryModule.{Boundary, ConfigBoundary}
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.jvm.boundary.gui.{InitGUI, SimulationGUI}
import monix.eval.Task
import monix.reactive.Observable

import java.nio.file.Path

object GUIModule:
  trait Provider:
    val gui: ConfigBoundary
  trait Component:
    class GUIBoundaryImpl extends ConfigBoundary:
      private val initSimulationScreen: InitGUI = InitGUI()
      private val simulationScreen: SimulationGUI = SimulationGUI()
      override def init(): Task[Unit] = initSimulationScreen.init()
      override def config(): Task[Path] = initSimulationScreen.config()
      override def error(err: ConfigurationError): Task[Unit] = initSimulationScreen.error(err)
      override def start(): Task[Unit] = initSimulationScreen.start(simulationScreen)
      override def render(i: Int): Task[Unit] = simulationScreen.render()
      override def events(): Observable[Event] = simulationScreen.events()
  trait Interface extends Provider with Component

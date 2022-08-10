package it.unibo.pps.js.boundary

import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import monix.reactive.Observable

import java.nio.file.Path

object JSGUIModule:
  trait Provider:
    val jsGui: ConfigBoundary
  trait Component:
    class JSGUIBoundaryImpl extends ConfigBoundary:
      private val guiJs = JSGUI()
      override def init() = guiJs.init()
      override def config(): Task[Path] = Task(Path.of(""))
      override def error(errors: Seq[ConfigurationError]): Task[Unit] = Task.pure {}
      override def start() = Task.pure {}
      override def consume(env: Environment) = guiJs.render(0)
      override def events(): Observable[Event] = guiJs.events()
  trait Interface extends Provider with Component

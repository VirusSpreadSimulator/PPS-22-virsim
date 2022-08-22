package it.unibo.pps.js.boundary

import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.boundary.{FilePath, StringFilePath}
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.js.boundary.gui.JSGUI
import monix.eval.Task
import monix.reactive.Observable

object JSGUIModule:
  trait Provider:
    val jsGui: ConfigBoundary
  trait Component:
    class JSGUIBoundaryImpl extends ConfigBoundary:
      private val guiJs = JSGUI()
      override def init() = guiJs.init()
      override def config(): Task[FilePath] = guiJs.config().map(JsFilePath.apply)
      override def error(errors: Seq[ConfigurationError]): Task[Unit] = guiJs.error(errors)
      override def start() = guiJs.start()
      override def stop() = guiJs.stop()
      override def consume(env: Environment) = guiJs.render(env)
      override def events(): Observable[Event] = guiJs.events()
  trait Interface extends Provider with Component

package it.unibo.pps.js.boundary.gui

import it.unibo.pps.boundary.ViewUtils.*
import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.control.parser.ReaderModule.FilePath
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import Values.Text
import it.unibo.pps.js.boundary.gui.component.MonadComponents.*
import it.unibo.pps.js.boundary.gui.panel.BottomPanels.{CommandPanel, DynamicConfigPanel, DynamicActionsLog, StatsPanel}
import it.unibo.pps.js.boundary.gui.panel.SimulationCanvas
import it.unibo.pps.js.parser.JSReader.JSFilePath
import monix.eval.Task
import monix.reactive.subjects.PublishSubject
import monix.reactive.{Consumer, Observable, OverflowStrategy}
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Div, Image}

trait JSGUI:
  def init(): Task[Unit]
  def config(): Task[FilePath]
  def error(errors: Seq[ConfigurationError]): Task[Unit]
  def start(): Task[Unit]
  def stop(): Task[Unit]
  def render(env: Environment): Task[Unit]
  def events(): Observable[Event]

object JSGUI:
  def apply(): JSGUI = JSGUIImpl()
  private class JSGUIImpl() extends JSGUI:
    private lazy val fileChosen = PublishSubject[FilePath]()
    private lazy val fileInput = dom.document.getElementById("file_input").asInstanceOf[dom.html.Input]
    // simulation
    private lazy val simulationCanvas = SimulationCanvas()
    // lateral
    private lazy val lateralGroupPanel = dom.document.getElementById("lateral-panel").asInstanceOf[Div]
    private lazy val commandPanel = CommandPanel()
    private lazy val dynamicConfigPanel = DynamicConfigPanel()
    private lazy val dynamicActionsLog = DynamicActionsLog()
    private lazy val statsPanel = StatsPanel()

    override def init(): Task[Unit] = io(fileInput.onchange = e => fileChosen.onNext(JSFilePath(fileInput.files(0))))

    override def config(): Task[FilePath] = Task.defer(fileChosen.consumeWith(Consumer.head))

    override def error(errors: Seq[ConfigurationError]): Task[Unit] = for
      errorMessage <- io(
        errors
          .map(err =>
            err match
              case ConfigurationError.INVALID_FILE(message) => Text.INVALID_FILE_LABEL + message
              case ConfigurationError.WRONG_PARAMETER(message) => Text.WRONG_PARAMETER_LABEL + message
          )
          .reduce(_ + "\n" + _)
      )
      _ <- io(dom.window.alert(errorMessage))
    yield ()

    override def start(): Task[Unit] = for
      _ <- io(fileInput.disabled = true)
      _ <- simulationCanvas.init()
      _ <- commandPanel.init()
      _ <- dynamicConfigPanel.init()
      _ <- io(lateralGroupPanel.style = "display: block")
    yield ()

    override def stop(): Task[Unit] = for
      _ <- commandPanel.stop()
      _ <- dynamicConfigPanel.stop()
    yield ()

    override def render(env: Environment): Task[Unit] = for
      _ <- simulationCanvas.update(env)
      _ <- dynamicActionsLog.update(env)
      _ <- statsPanel.update(env)
    yield ()

    override def events(): Observable[Event] =
      Observable.fromIterable(Seq(commandPanel, dynamicConfigPanel)).mergeMap(_.events)

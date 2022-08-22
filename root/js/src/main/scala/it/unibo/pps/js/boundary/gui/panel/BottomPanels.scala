package it.unibo.pps.js.boundary.gui.panel

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.js.boundary.Values.Text
import it.unibo.pps.js.boundary.Values.BootstrapClasses
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.js.boundary.component.MonadComponents.MonadButton
import it.unibo.pps.js.boundary.gui.panel.Panels.{BasePanel, EventablePanel}
import it.unibo.pps.boundary.ViewUtils.io
import monix.eval.Task
import monix.reactive.Observable
import org.scalajs.dom

/** Module that wrap all the panels that are in the bottom area of the simulation gui */
object BottomPanels:
  class CommandPanel extends BasePanel with EventablePanel:
    private lazy val pauseBtn: MonadButton =
      MonadButton(
        Text.PAUSE_BTN,
        Pause,
        BootstrapClasses.PAUSE_BTN,
        (_, b) => { b.disabled = true; resumeBtn.button.removeAttribute("disabled") }
      )
    private lazy val resumeBtn: MonadButton =
      MonadButton(
        Text.RESUME_BTN,
        Resume,
        BootstrapClasses.RESUME_BTN,
        (_, b) => { b.disabled = true; pauseBtn.button.removeAttribute("disabled") }
      )
    private lazy val stopBtn: MonadButton = MonadButton(Text.STOP_BTN, Stop, BootstrapClasses.STOP_BTN)

    override def init(): Task[Unit] = for
      _ <- io(dom.document.getElementById("commands-group").appendChild(pauseBtn.button))
      _ <- io(dom.document.getElementById("commands-group").appendChild(resumeBtn.button))
      _ <- io(dom.document.getElementById("commands-group").appendChild(stopBtn.button))
      _ <- io(resumeBtn.button.disabled = true)
    yield ()

    override def stop(): Task[Unit] = io {
      for elem <- Seq(pauseBtn.button, resumeBtn.button, stopBtn.button) do elem.disabled = true
    }

    override lazy val events: Observable[Event] =
      Observable.fromIterable(Seq(pauseBtn, resumeBtn, stopBtn)).mergeMap(_.events)

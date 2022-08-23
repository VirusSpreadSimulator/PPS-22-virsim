package it.unibo.pps.js.boundary.gui.panel

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.boundary.component.Events.{Event, Params}
import it.unibo.pps.js.boundary.gui.Values.{BootstrapClasses, Text}
import it.unibo.pps.js.boundary.gui.component.MonadComponents.{MonadButton, MonadConfigButton, MonadSelect}
import it.unibo.pps.js.boundary.gui.panel.Panels.{BasePanel, EventablePanel}
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
    private lazy val speedSelect: MonadSelect = MonadSelect(Params.Speed.values, Params.Speed.NORMAL, ChangeSpeed.apply)

    override def init(): Task[Unit] = for
      parent <- io(dom.document.getElementById("commands-group"))
      _ <- io(parent.appendChild(pauseBtn.button))
      _ <- io(parent.appendChild(resumeBtn.button))
      _ <- io(parent.appendChild(stopBtn.button))
      _ <- io(parent.appendChild(speedSelect.select))
      _ <- io(resumeBtn.button.disabled = true)
    yield ()

    override def stop(): Task[Unit] = for
      _ <- io(for elem <- Seq(pauseBtn.button, resumeBtn.button, stopBtn.button) do elem.disabled = true)
      _ <- io(speedSelect.select.disabled = true)
    yield ()

    override lazy val events: Observable[Event] =
      Observable.fromIterable(Seq(pauseBtn, resumeBtn, stopBtn, speedSelect)).mergeMap(_.events)

  class DynamicConfigPanel extends BasePanel with EventablePanel:
    private lazy val switchMask: MonadButton =
      MonadButton(Text.SWITCH_MASK_OBLIGATION, SwitchMaskObligation, BootstrapClasses.BTN_SECONDARY)
    private lazy val switchStructure: MonadConfigButton =
      MonadConfigButton(Text.SWITCH_STRUCTURE_OPEN, 5, SwitchStructure.apply)
    private lazy val vaccineRound: MonadConfigButton =
      MonadConfigButton.numeric(
        Text.VACCINE_ROUND,
        5,
        0,
        100,
        p => VaccineRound(Some(p).filter(_.nonEmpty).map(_.toDouble).filter(n => n >= 0 && n <= 100).getOrElse(0))
      )

    override def init(): Task[Unit] = for
      parent <- io(dom.document.getElementById("dynamic-actions-group"))
      _ <- io(parent.appendChild(switchMask.button))
      _ <- io(parent.appendChild(vaccineRound.panel))
      _ <- io(parent.appendChild(switchStructure.panel))
    yield ()

    override def stop(): Task[Unit] = for
      _ <- io(for elem <- Seq(switchMask.button, switchStructure.button, vaccineRound.button) do elem.disabled = true)
    yield ()

    override lazy val events: Observable[Event] =
      Observable.fromIterable(Seq(switchMask, switchStructure, vaccineRound)).mergeMap(_.events)

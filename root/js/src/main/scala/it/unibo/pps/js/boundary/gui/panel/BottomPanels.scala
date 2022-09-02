package it.unibo.pps.js.boundary.gui.panel

import it.unibo.pps.boundary.ViewUtils.{StatsDisplayer, io}
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.boundary.component.Events.{Event, Params}
import it.unibo.pps.control.loader.extractor.HospitalStats.HospitalPressure
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Groupable}
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.js.boundary.gui.Values.{BootstrapClasses, Text}
import it.unibo.pps.js.boundary.gui.component.MonadComponents.{MonadButton, MonadConfigButton, MonadSelect}
import it.unibo.pps.boundary.component.panel.Panels.{BasePanel, EventablePanel, UpdatablePanel}
import it.unibo.pps.entity.common.Utils.select
import org.scalajs.dom.html.Span
import monix.eval.Task
import monix.reactive.Observable
import org.scalajs.dom

/** Module that wraps all the util panels of the simulation gui. */
object BottomPanels:
  /** Command Panel implementation. It is the panel that contains the pause/resume/stop commands of the simulation and
    * the control to change the speed.
    */
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

  /** Dynamic Configuration Panel. It is the panel that contains all the possible dynamic configurations set by the
    * user.
    */
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
      _ <- io(for elem <- Seq(switchStructure.input, vaccineRound.input) do elem.disabled = true)
    yield ()

    override lazy val events: Observable[Event] =
      Observable.fromIterable(Seq(switchMask, switchStructure, vaccineRound)).mergeMap(_.events)

  /** DynamicActionsLog. It is the panel that show all the information about the dynamic configurations. */
  class DynamicActionsLog extends UpdatablePanel:
    private lazy val maskLabel = dom.document.getElementById("mask-status").asInstanceOf[Span]
    private lazy val structuresLabel = dom.document.getElementById("groups-status").asInstanceOf[Span]

    override def init(): Task[Unit] = Task.pure {}

    override def update(env: Environment): Task[Unit] = for
      maskStatus <- io(if env.allEntities.forall(_.hasMask) then Text.YES else Text.NO)
      groupStatus <- io(
        env.structures
          .select[SimulationStructure with Closable with Groupable]
          .groupBy(_.group)
          .map((k, v) => (k, if v.forall(_.isOpen) then Text.OPEN_STRUCTURE else Text.CLOSED_STRUCTURE))
      )
      _ <- io(maskLabel.textContent = maskStatus)
      _ <- io(structuresLabel.innerHTML = groupStatus.mkString("<br>- ", "<br>- ", ""))
    yield ()

  /** StatsPanel. It is the panel that show the main statistics about the simulation data. */
  class StatsPanel extends UpdatablePanel:
    import it.unibo.pps.control.loader.extractor.EntitiesStats.{Alive, AtHome, Deaths, Infected, Sick}
    import it.unibo.pps.control.loader.extractor.EnvironmentStats.{Days, Time}

    private lazy val stats = Seq(
      StatsDisplayer(dom.document.getElementById("days").asInstanceOf[Span], Days()),
      StatsDisplayer(dom.document.getElementById("time").asInstanceOf[Span], Time()),
      StatsDisplayer(dom.document.getElementById("alive").asInstanceOf[Span], Alive()),
      StatsDisplayer(dom.document.getElementById("deaths").asInstanceOf[Span], Deaths()),
      StatsDisplayer(dom.document.getElementById("infected").asInstanceOf[Span], Infected()),
      StatsDisplayer(dom.document.getElementById("sick").asInstanceOf[Span], Sick()),
      StatsDisplayer(dom.document.getElementById("hospital-pressure").asInstanceOf[Span], HospitalPressure()),
      StatsDisplayer(dom.document.getElementById("at-home").asInstanceOf[Span], AtHome())
    )

    override def init(): Task[Unit] = Task.pure {}

    override def update(env: Environment): Task[Unit] =
      io(stats.foreach(stat => stat.label.textContent = stat.extractor.extractData(env).toString))

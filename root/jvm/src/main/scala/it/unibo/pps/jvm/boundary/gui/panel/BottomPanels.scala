package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.boundary.ViewUtils.{StatsDisplayer, io}
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.boundary.component.Events.{Event, Params}
import it.unibo.pps.boundary.component.panel.Panels.{BasePanel, UpdatablePanel, EventablePanel}
import it.unibo.pps.control.loader.extractor.EntitiesStats.*
import it.unibo.pps.control.loader.extractor.EnvironmentStats.{Days, Time}
import it.unibo.pps.control.loader.extractor.HospitalStats.HospitalPressure
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Groupable}
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.jvm.boundary.gui.Values.{Dimension, Text}
import it.unibo.pps.jvm.boundary.gui.component.MonadComponents.{MonadButton, MonadCombobox, MonadConfigButton}
import monix.eval.Task
import monix.reactive.Observable
import java.awt.{BorderLayout, Component, Font}
import javax.swing.*
import javax.swing.text.DefaultCaret

/** Module that wraps all the panels that are in the bottom area of the simulation gui. */
object BottomPanels:
  /** Command Panel implementation. It is the panel that contains the pause/resume/stop commands of the simulation and
    * the control to change the speed.
    */
  class CommandPanel extends JPanel with BasePanel with EventablePanel:
    private val pauseBtn: MonadButton =
      MonadButton(Text.PAUSE_BTN, Pause, (_, b) => { b.setEnabled(false); resumeBtn.button.setEnabled(true) })
    private val resumeBtn: MonadButton =
      MonadButton(Text.RESUME_BTN, Resume, (_, b) => { b.setEnabled(false); pauseBtn.button.setEnabled(true) })
    private val stopBtn = MonadButton(Text.STOP_BTN, Stop)
    private val speedComboBox = MonadCombobox(Params.Speed.values, Params.Speed.NORMAL, ChangeSpeed.apply)

    override def init(): Task[Unit] =
      for
        _ <- setVerticalPanel(
          this,
          Text.COMMANDS_LABEL,
          Seq(pauseBtn.button, resumeBtn.button, stopBtn.button, speedComboBox.combobox)
        )
        _ <- io(resumeBtn.button.setEnabled(false))
      yield ()

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(pauseBtn, resumeBtn, stopBtn, speedComboBox))
        .mergeMap(_.events)

    override def stop(): Task[Unit] =
      io {
        for elem <- Seq(pauseBtn.button, resumeBtn.button, stopBtn.button, speedComboBox.combobox) do
          elem.setEnabled(false)
      }

  /** Dynamic Configuration Panel. It is the panel that contains all the possible dynamic configurations set by the
    * user.
    */
  class DynamicConfigPanel extends JPanel with BasePanel with EventablePanel:
    private val turnMaskOn = MonadButton(Text.SWITCH_MASK_OBLIGATION, SwitchMaskObligation)
    private val switchStructureBtn =
      MonadConfigButton(Text.SWITCH_STRUCTURE_OPEN, Dimension.TEXT_FIELD_LENGTH, SwitchStructure.apply)
    private val vaccineRound = MonadConfigButton.numeric(
      Text.VACCINE_ROUND,
      Dimension.NUMERIC_FIELD_LENGTH,
      0,
      100,
      p => VaccineRound(Some(p).filter(_.nonEmpty).map(_.toDouble).getOrElse(0))
    )

    override def init(): Task[Unit] =
      setVerticalPanel(
        this,
        Text.DYNAMIC_CONFIG_LABEL,
        Seq(turnMaskOn.button, vaccineRound.panel, switchStructureBtn.panel)
      )

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(turnMaskOn, vaccineRound, switchStructureBtn))
        .mergeMap(_.events)

    override def stop(): Task[Unit] =
      io {
        for
          elem <- Seq(
            turnMaskOn.button,
            switchStructureBtn.button,
            vaccineRound.button,
            switchStructureBtn.textField,
            vaccineRound.textField
          )
        do elem.setEnabled(false)
      }

  /** DynamicActionsLog. It is the panel that show all the information about the dynamic configurations. */
  class DynamicActionsLog extends JPanel with UpdatablePanel:
    private lazy val textArea = JEditorPane("text/html", "")
    private lazy val scrollTextArea = JScrollPane(textArea)

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BorderLayout()))
        titleLabel = JLabel(Text.DYNAMIC_CONFIG_LOG_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        _ <- io(add(titleLabel, BorderLayout.NORTH))
        _ <- io(textArea.setEditable(false))
        _ <- io(scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED))
        _ <- io(textArea.getCaret.asInstanceOf[DefaultCaret].setUpdatePolicy(DefaultCaret.NEVER_UPDATE))
        _ <- io(add(scrollTextArea, BorderLayout.CENTER))
      yield ()

    override def update(env: Environment): Task[Unit] = for
      maskStatus <- io(if env.allEntities.forall(_.hasMask) then Text.YES else Text.NO)
      groupStatus <- io(
        env.structures
          .select[SimulationStructure with Closable with Groupable]
          .groupBy(_.group)
          .map((k, v) => (k, if v.forall(_.isOpen) then Text.OPEN_STRUCTURE else Text.CLOSED_STRUCTURE))
      )
      _ <- io(
        textArea.setText(
          s"${Text.MASK_STATUS_TITLE}: $maskStatus<br>${Text.STRUCTURES_GROUP_STATUS_TITLE}:<br>${groupStatus
            .mkString("- ", "<br>- ", "")}"
        )
      )
    yield ()

  /** StatsPanel. It is the panel that show the main statistics about the simulation data. */
  class StatsPanel extends JPanel with UpdatablePanel:

    private lazy val stats = Seq(
      StatsDisplayer(JLabel(Text.DAYS_LABEL_TITLE), Days(), Text.DAYS_LABEL_TITLE),
      StatsDisplayer(JLabel(Text.TIME_LABEL_TITLE), Time(), Text.TIME_LABEL_TITLE),
      StatsDisplayer(JLabel(Text.INFECTED_LABEL_TITLE), Infected(), Text.INFECTED_LABEL_TITLE),
      StatsDisplayer(JLabel(Text.SICK_LABEL_TITLE), Sick(), Text.SICK_LABEL_TITLE),
      StatsDisplayer(JLabel(Text.DEATHS_LABEL_TITLE), Deaths(), Text.DEATHS_LABEL_TITLE),
      StatsDisplayer(JLabel(Text.ALIVE_LABEL_TITLE), Alive(), Text.ALIVE_LABEL_TITLE),
      StatsDisplayer(JLabel(Text.AT_HOME_LABEL_TITLE), AtHome(), Text.AT_HOME_LABEL_TITLE),
      StatsDisplayer(JLabel(Text.HOSPITAL_PRESSURE_LABEL_TITLE), HospitalPressure(), Text.HOSPITAL_PRESSURE_LABEL_TITLE)
    )

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BoxLayout(this, BoxLayout.Y_AXIS)))
        titleLabel = JLabel(Text.STATS_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        _ <- io(add(titleLabel, Component.TOP_ALIGNMENT))
        _ <- io {
          for elem <- stats do
            add(elem.label)
            elem.label.setAlignmentX(Component.LEFT_ALIGNMENT)
        }
      yield ()

    override def update(env: Environment): Task[Unit] =
      io(for elem <- stats do elem.label.setText(elem.defaultName + elem.extractor.extractData(env).toString))

  /** Utils to set the panel as a vertical panel with a title and left-aligned elements.
    * @param panel
    *   the panel to set
    * @param title
    *   the title
    * @param elems
    *   the elements to add
    * @return
    *   the task
    */
  private def setVerticalPanel(panel: JPanel, title: String, elems: Seq[JComponent]): Task[Unit] =
    for
      _ <- io(panel.setLayout(BoxLayout(panel, BoxLayout.Y_AXIS)))
      titleLabel = JLabel(title)
      _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
      elemsToAdd = titleLabel +: elems
      _ <- io {
        for elem <- elemsToAdd do
          panel.add(elem)
          elem.setAlignmentX(Component.LEFT_ALIGNMENT)
      }
    yield ()

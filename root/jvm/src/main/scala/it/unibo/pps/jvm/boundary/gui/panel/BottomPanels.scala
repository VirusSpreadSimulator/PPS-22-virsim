package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.component.MonadComponents.{MonadButton, MonadCombobox, MonadConfigButton}
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.boundary.component.Events.{Event, Params}
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.jvm.boundary.gui.panel.Panels.{DisplayblePanel, EventablePanel, UpdateblePanel}
import monix.reactive.Observable
import monix.eval.Task
import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.jvm.boundary.exporter.Extractors.{
  DataExtractor,
  Deaths,
  HospitalPressure,
  Days,
  Infected,
  Sick,
  Alive
}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment

import java.awt.{BorderLayout, Component, Font}
import javax.swing.{BoxLayout, JLabel, JPanel, JScrollPane, JTextArea, JTextField, ScrollPaneConstants}

/** Module that wrap all the panels that are in the bottom area of the simulation gui */
object BottomPanels:
  /** Command Panel implementation. It is the panel that contains the pause/stop commands of the simulation */
  class CommandPanel extends DisplayblePanel with EventablePanel:
    private val pauseBtn: MonadButton =
      MonadButton(Text.PAUSE_BTN, Pause, (_, b) => { b.setEnabled(false); resumeBtn.button.setEnabled(true) })
    private val resumeBtn: MonadButton =
      MonadButton(Text.RESUME_BTN, Resume, (_, b) => { b.setEnabled(false); pauseBtn.button.setEnabled(true) })
    private val stopBtn = MonadButton(Text.STOP_BTN, Stop)
    private val speedComboBox = MonadCombobox(Params.Speed.values, Params.Speed.NORMAL, ChangeSpeed.apply)

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BoxLayout(this, BoxLayout.Y_AXIS)))
        titleLabel = JLabel(Text.COMMANDS_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        elems = Seq(titleLabel, pauseBtn.button, resumeBtn.button, stopBtn.button, speedComboBox.combobox)
        _ <- io {
          for elem <- elems do
            add(elem)
            elem.setAlignmentX(Component.LEFT_ALIGNMENT)
        }
        _ <- io(resumeBtn.button.setEnabled(false))
      yield ()

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(pauseBtn, resumeBtn, stopBtn, speedComboBox))
        .mergeMap(_.events)

    override def stop(): Task[Unit] =
      Task {
        for elem <- Seq(pauseBtn.button, resumeBtn.button, stopBtn.button, speedComboBox.combobox) do
          elem.setEnabled(false)
      }

  /** Dynamic Configuration Panel. It is the panel that contains all the possible dynamic configuration set by the user.
    */
  class DynamicConfigPanel extends DisplayblePanel with EventablePanel:
    private val turnMaskOn = MonadButton(Text.SWITCH_MASK_OBLIGATION, SwitchMaskObligation)
    private val switchStructureBtn = MonadConfigButton(Text.SWITCH_STRUCTURE_OPEN, 5, SwitchStructure.apply)
    private val vaccineRound = MonadConfigButton.numeric(
      Text.VACCINE_ROUND,
      3,
      0,
      100,
      p => VaccineRound(Some(p).filter(_.nonEmpty).map(_.toDouble).getOrElse(0))
    )

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BoxLayout(this, BoxLayout.Y_AXIS)))
        titleLabel = JLabel(Text.DYNAMIC_CONFIG_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        elems = Seq(titleLabel, turnMaskOn.button, vaccineRound.panel, switchStructureBtn.panel)
        _ <- io {
          for elem <- elems do
            add(elem)
            elem.setAlignmentX(Component.LEFT_ALIGNMENT)
        }
      yield ()

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(turnMaskOn, vaccineRound, switchStructureBtn))
        .mergeMap(_.events)

    override def stop(): Task[Unit] =
      Task(for elem <- Seq(turnMaskOn.button, switchStructureBtn.button, vaccineRound.button) do elem.setEnabled(false))

  /** DynamicActionsLog. It is the panel that show all the information about the dynamic configurations. */
  class DynamicActionsLog extends UpdateblePanel:
    private lazy val textArea = JTextArea("a \n a \n a \n")
    private lazy val scrollTextArea = JScrollPane(textArea)

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BorderLayout()))
        titleLabel = JLabel(Text.DYNAMIC_CONFIG_LOG_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        _ <- io(add(titleLabel, BorderLayout.NORTH))
        _ <- io(textArea.setEditable(false))
        _ <- io(scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED))
        _ <- io(add(scrollTextArea, BorderLayout.CENTER))
      yield ()

    override def update(env: Environment): Task[Unit] = io(
      textArea.setText((for i <- 1 to 30 yield "a \n a \n a").reduce(_ + _))
    )

  /** StatsPanel. It is the panel that show the main statistics about the simulation data. */
  class StatsPanel extends UpdateblePanel:
    private lazy val daysLabel: JLabel = new JLabel(Text.DAYS_LABEL_TITLE)
    private lazy val aliveLabel: JLabel = new JLabel(Text.ALIVE_LABEL_TITLE)
    private lazy val deathsLabel: JLabel = new JLabel(Text.DEATHS_LABEL_TITLE)
    private lazy val infectedLabel: JLabel = new JLabel(Text.INFECTED_LABEL_TITLE)
    private lazy val sickLabel: JLabel = new JLabel(Text.SICK_LABEL_TITLE)
    private lazy val hospitalPressure: JLabel = new JLabel(Text.HOSPITAL_PRESSURE_LABEL_TITLE)

    private val daysExtractor: DataExtractor[Long] = Days()
    private val aliveExtractor: DataExtractor[Int] = Alive()
    private val deathsExtractor: DataExtractor[Int] = Deaths()
    private val infectedExtractor: DataExtractor[Int] = Infected()
    private val sickExtractor: DataExtractor[Int] = Sick()
    private val hospitalPressureExtractor: DataExtractor[Double] = HospitalPressure()

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BoxLayout(this, BoxLayout.Y_AXIS)))
        titleLabel = JLabel(Text.STATS_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        _ <- io(add(titleLabel, Component.TOP_ALIGNMENT))
        elems = Seq(daysLabel, aliveLabel, deathsLabel, infectedLabel, sickLabel, hospitalPressure)
        _ <- io {
          for elem <- elems do
            add(elem)
            elem.setAlignmentX(Component.LEFT_ALIGNMENT)
        }
      yield ()

    override def update(env: Environment): Task[Unit] =
      for
        _ <- io(daysLabel.setText(Text.DAYS_LABEL_TITLE + daysExtractor.extractData(env)).toString)
        _ <- io(aliveLabel.setText(Text.ALIVE_LABEL_TITLE + aliveExtractor.extractData(env).toString))
        _ <- io(deathsLabel.setText(Text.DEATHS_LABEL_TITLE + deathsExtractor.extractData(env).toString))
        _ <- io(infectedLabel.setText(Text.INFECTED_LABEL_TITLE + infectedExtractor.extractData(env).toString))
        _ <- io(sickLabel.setText(Text.SICK_LABEL_TITLE + sickExtractor.extractData(env).toString))
        _ <- io(
          hospitalPressure.setText(
            Text.HOSPITAL_PRESSURE_LABEL_TITLE + hospitalPressureExtractor.extractData(env).toString
          )
        )
      yield ()

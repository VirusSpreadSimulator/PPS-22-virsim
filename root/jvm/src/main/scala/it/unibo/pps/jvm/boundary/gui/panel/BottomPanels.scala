package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.component.MonadComponents.{MonadButton, MonadConfigButton}
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.jvm.boundary.gui.panel.Panels.{DisplayblePanel, EventablePanel, UpdateblePanel}
import monix.reactive.Observable
import monix.eval.Task
import it.unibo.pps.boundary.ViewUtils.io
import java.awt.{BorderLayout, Component, Font}
import javax.swing.{BoxLayout, JLabel, JPanel, JScrollPane, JTextArea, ScrollPaneConstants}

/** Module that wrap all the panels that are in the bottom area of the simulation gui */
object BottomPanels:
  /** Command Panel implementation. It is the panel that contains the pause/stop commands of the simulation */
  class CommandPanel extends DisplayblePanel with EventablePanel:
    private val pauseBtn = MonadButton(Text.PAUSE_BTN, Pause)
    private val stopBtn = MonadButton(Text.STOP_BTN, Stop)

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BoxLayout(this, BoxLayout.Y_AXIS)))
        titleLabel = JLabel(Text.COMMANDS_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        _ <- io(add(titleLabel))
        _ <- io(add(pauseBtn.button))
        _ <- io(add(stopBtn.button))
      yield ()

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(pauseBtn, stopBtn))
        .flatMap(_.events)

  /** Dynamic Configuration Panel. It is the panel that contains all the possible dynamic configuration set by the user.
    */
  class DynamicConfigPanel extends DisplayblePanel with EventablePanel:
    private val turnMaskOn = MonadButton(Text.SWITCH_MASK_OBLIGATION, SwitchMaskObligation)
    private val vaccineRound = MonadConfigButton.numeric(Text.VACCINE_ROUND, 3, 0, 100, p => VaccineRound(p.toDouble))
    private val switchStructureBtn = MonadConfigButton(Text.SWITCH_STRUCTURE_OPEN, 5, SwitchStructure.apply)

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
        .flatMap(_.events)

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
        _ <- io(scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED))
        _ <- io(add(scrollTextArea, BorderLayout.CENTER))
      yield ()

    override def update(): Task[Unit] = io(textArea.setText((for i <- 1 to 30 yield "a \n a \n a").reduce(_ + _)))

  /** StatsPanel. It is the panel that show the main statistics about the simulation data. */
  class StatsPanel extends UpdateblePanel:
    private lazy val textArea = JTextArea("a \n a \n a \n")
    private lazy val scrollTextArea = JScrollPane(textArea)

    override def init(): Task[Unit] =
      for
        _ <- io(setLayout(BorderLayout()))
        titleLabel = JLabel(Text.STATS_LABEL)
        _ <- io(titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD)))
        _ <- io(add(titleLabel, BorderLayout.NORTH))
        _ <- io(scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED))
        _ <- io(add(scrollTextArea, BorderLayout.CENTER))
      yield ()

    override def update(): Task[Unit] = io(textArea.setText((for i <- 1 to 30 yield "b \n b \n b").reduce(_ + _)))

package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.component.MonadComponents.{MonadButton, MonadConfigButton}
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.jvm.boundary.gui.panel.Panels.{DisplayblePanel, EventablePanel, UpdateblePanel}
import monix.reactive.Observable

import java.awt.{BorderLayout, Component, Font}
import javax.swing.{BoxLayout, JLabel, JPanel, JScrollPane, JTextArea, ScrollPaneConstants}

/** Module that wrap all the panels that are in the bottom area of the simulation gui */
object BottomPanels:
  /** Command Panel implementation. It is the panel that contains the pause/stop commands of the simulation */
  class CommandPanel extends DisplayblePanel with EventablePanel:
    private val pauseBtn = MonadButton(Text.PAUSE_BTN, Pause)
    private val stopBtn = MonadButton(Text.STOP_BTN, Stop)

    override def init(): Unit =
      setLayout(BoxLayout(this, BoxLayout.Y_AXIS))
      val titleLabel = JLabel(Text.COMMANDS_LABEL)
      titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD))
      add(titleLabel)
      add(pauseBtn.button)
      add(stopBtn.button)

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

    override def init(): Unit =
      setLayout(BoxLayout(this, BoxLayout.Y_AXIS))
      val titleLabel = JLabel(Text.DYNAMIC_CONFIG_LABEL)
      titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD))
      for elem <- Seq(titleLabel, turnMaskOn.button, vaccineRound.panel, switchStructureBtn.panel) do
        add(elem)
        elem.setAlignmentX(Component.LEFT_ALIGNMENT)

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(turnMaskOn, vaccineRound, switchStructureBtn))
        .flatMap(_.events)

  /** DynamicActionsLog. It is the panel that show all the information about the dynamic configurations. */
  class DynamicActionsLog extends UpdateblePanel:
    private lazy val textArea = JTextArea("a \n a \n a \n")
    private lazy val scrollTextArea = JScrollPane(textArea)
    private lazy val titleLabel = JLabel(Text.DYNAMIC_CONFIG_LOG_LABEL)

    override def init(): Unit =
      setLayout(BorderLayout())
      titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD))
      add(titleLabel, BorderLayout.NORTH)
      scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
      add(scrollTextArea, BorderLayout.CENTER)

    override def updateAndDisplay(): Unit =
      textArea.setText((for i <- 1 to 30 yield "a \n a \n a").reduce(_ + _))

  /** StatsPanel. It is the panel that show the main statistics about the simulation data. */
  class StatsPanel extends UpdateblePanel:
    private lazy val textArea = JTextArea("a \n a \n a \n")
    private lazy val scrollTextArea = JScrollPane(textArea)
    private lazy val titleLabel = JLabel(Text.STATS_LABEL)

    override def init(): Unit =
      setLayout(BorderLayout())
      titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD))
      add(titleLabel, BorderLayout.NORTH)
      scrollTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
      add(scrollTextArea, BorderLayout.CENTER)

    override def updateAndDisplay(): Unit =
      textArea.setText((for i <- 1 to 30 yield "b \n b \n b").reduce(_ + _))

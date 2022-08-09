package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.component.MonadComponents.{MonadButton, MonadConfigButton}
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.jvm.boundary.gui.panel.Panels.{DisplayblePanel, EventablePanel}
import monix.reactive.Observable

import java.awt.Font
import javax.swing.{BoxLayout, JLabel, JPanel}

/** Module that wrap all the panels that are in the bottom area of the simulation gui */
object BottomPanels:
  /** Command Panel implementation. It is the panel that contains the pause/stop commands of the simulation */
  class CommandPanel extends DisplayblePanel with EventablePanel:
    private val pauseBtn = MonadButton(Text.PAUSE_BTN, Pause)
    private val stopBtn = MonadButton(Text.STOP_BTN, Stop)

    override def display(): Unit =
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

    override def display(): Unit =
      setLayout(BoxLayout(this, BoxLayout.Y_AXIS))
      val titleLabel = JLabel(Text.DYNAMIC_CONFIG_LABEL)
      titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD))
      add(titleLabel)
      add(turnMaskOn.button)
      add(vaccineRound.panel)
      add(switchStructureBtn.panel)

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(turnMaskOn, vaccineRound, switchStructureBtn))
        .flatMap(_.events)

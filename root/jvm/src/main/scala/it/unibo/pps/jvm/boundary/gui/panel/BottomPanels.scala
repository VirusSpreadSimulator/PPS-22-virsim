package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.component.MonadComponents.{MonadButton, MonadConfigButton}
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.jvm.boundary.gui.panel.Panels.{DisplayblePanel, EventablePanel}
import monix.reactive.Observable

import java.awt.Font
import javax.swing.{BoxLayout, JLabel, JPanel}

object BottomPanels:
  class CommandPanel extends DisplayblePanel with EventablePanel:
    private val pauseBtn = MonadButton(Text.PAUSEBTN, Pause)

    override def display(): Unit =
      setLayout(BoxLayout(this, BoxLayout.Y_AXIS))
      val titleLabel = JLabel(Text.COMMANDSLABEL)
      titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD))
      add(titleLabel)
      add(pauseBtn.button)

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(pauseBtn))
        .flatMap(_.events)

  class DynamicConfigPanel extends DisplayblePanel with EventablePanel:
    private val switchStructureBtn = MonadConfigButton("Switch group", 5, SwitchStructure.apply)

    override def display(): Unit =
      setLayout(BoxLayout(this, BoxLayout.Y_AXIS))
      val titleLabel = JLabel(Text.DYBAMICONFIGLABEL)
      titleLabel.setFont(titleLabel.getFont.deriveFont(Font.BOLD))
      add(titleLabel)
      add(switchStructureBtn.panel)

    override lazy val events: Observable[Event] =
      Observable
        .fromIterable(Seq(switchStructureBtn))
        .flatMap(_.events)

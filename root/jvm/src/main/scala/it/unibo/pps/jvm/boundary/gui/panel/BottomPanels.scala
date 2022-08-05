package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.component.MonadButton
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import javax.swing.JPanel

object BottomPanels:
  class CommandPanel extends JPanel:
    private val pauseBtn = MonadButton(Text.PAUSEBTN, Pause)

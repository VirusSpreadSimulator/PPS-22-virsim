package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.gui.panel.Panels.UpdateblePanel
import javax.swing.{JLabel, JPanel}

/** The Chart Panel is the panel of the simulation gui that contains and handle the graphs. */
class ChartPanel() extends UpdateblePanel:
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))

  override def init(): Unit = repaint()

  override def updateAndDisplay(): Unit = repaint()

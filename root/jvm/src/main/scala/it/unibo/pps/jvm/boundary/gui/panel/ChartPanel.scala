package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.gui.panel.Panels.UpdateblePanel
import javax.swing.{JLabel, JPanel}

class ChartPanel() extends UpdateblePanel:
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))

  override def updateAndDisplay(): Unit = repaint()

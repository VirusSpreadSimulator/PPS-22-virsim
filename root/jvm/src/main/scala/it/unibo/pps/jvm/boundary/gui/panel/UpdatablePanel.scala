package it.unibo.pps.jvm.boundary.gui.panel

import javax.swing.JPanel

trait UpdateblePanel extends JPanel:
  def updateAndDisplay(): Unit

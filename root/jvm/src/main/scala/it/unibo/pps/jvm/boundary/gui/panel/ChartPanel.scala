package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.gui.panel.Panels.UpdateblePanel
import javax.swing.{JLabel, JPanel}
import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task

/** The Chart Panel is the panel of the simulation gui that contains and handle the graphs. */
class ChartPanel() extends UpdateblePanel:
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))

  override def init(): Task[Unit] = io(this.repaint())

  override def update(env: Environment): Task[Unit] = io(this.repaint())

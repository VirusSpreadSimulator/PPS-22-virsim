package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.gui.panel.Panels.UpdateblePanel

import javax.swing.{JLabel, JPanel}
import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.Values.SimulationColor
import monix.eval.Task

/** The Chart Panel is the panel of the simulation gui that contains and handle the graphs. */
class ChartPanel() extends UpdateblePanel:
  setBackground(SimulationColor.BACKGROUND_CHART_PANEL_COLOR)
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))
  add(JLabel("Ciao"))

  override def init(): Task[Unit] = for _ <- io(setOpaque(true))
  yield ()

  override def update(env: Environment): Task[Unit] = Task.pure {}

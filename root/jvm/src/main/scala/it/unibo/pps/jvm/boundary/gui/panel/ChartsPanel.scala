package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.Values.SimulationColor
import it.unibo.pps.jvm.boundary.gui.panel.Panels.{DisplayblePanel, UpdateblePanel}
import it.unibo.pps.jvm.boundary.gui.panel.charts.{BarChart, LineChart, PieChart}
import it.unibo.pps.jvm.boundary.gui.panel.charts.Charts.Chart
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import it.unibo.pps.boundary.ViewUtils.io

import java.awt.{Dimension, FlowLayout}
import monix.eval.Task

/** The ChartsPanel is the panel of the simulation gui that contains and handle the graphs. */
class ChartsPanel extends UpdateblePanel:

  setBackground(SimulationColor.BACKGROUND_CHART_PANEL_COLOR)
  private val charts: List[Chart] = List(BarChart(), LineChart(), PieChart())

  override def init(): Task[Unit] =
    for
      _ <- io(setOpaque(true))
      _ <- io(setLayout(FlowLayout(FlowLayout.CENTER, 0, 0)))
      _ <- Task.sequence(charts.map(_.init()))
      _ <- io {
        for chart <- charts do add(chart.chartPanel)
      }
    yield ()

  override def update(env: Environment): Task[Unit] =
    for _ <- Task.sequence(charts.map(_.update(env)))
    yield ()

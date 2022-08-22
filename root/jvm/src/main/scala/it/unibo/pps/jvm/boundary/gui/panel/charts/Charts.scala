package it.unibo.pps.jvm.boundary.gui.panel.charts

import it.unibo.pps.jvm.boundary.exporter.Extractors.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import org.jfree.chart.{ChartPanel, JFreeChart}

import java.awt.Dimension

object Charts:

  /** The trait used to manage Charts in the simulation. Every chart needs to be initialized and then updated with
    * environment values.
    */
  trait Chart:
    def chart: JFreeChart
    def chartPanel: ChartPanel
    def init(): Task[Unit]
    def update(env: Environment): Task[Unit]

  /** Wrapper for JFreeChart ChartPanel that allows to adapt the dimension of the panel et every size change.
    * @param jFreeChart
    *   the chart inside the ChartPanel.
    */
  class MyChartPanel(val jFreeChart: JFreeChart) extends ChartPanel(jFreeChart):
    override def getPreferredSize: Dimension =
      new Dimension(this.getParent.getWidth, this.getParent.getHeight / 3)

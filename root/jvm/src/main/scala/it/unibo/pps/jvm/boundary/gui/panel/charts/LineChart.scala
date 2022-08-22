package it.unibo.pps.jvm.boundary.gui.panel.charts

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.jvm.boundary.exporter.Extractors.{DataExtractor, Infected}
import it.unibo.pps.jvm.boundary.exporter.StatisticalData.Stats
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.Values.SimulationColor
import it.unibo.pps.jvm.boundary.gui.panel.charts.Charts.{Chart, MyChartPanel}
import monix.eval.Task
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}

import java.awt.Color
import java.awt.Font

class LineChart extends Chart:
  private val series: XYSeries = new XYSeries("Index")
  private val dataset: XYDataset = new XYSeriesCollection(series)
  private val lineChart: JFreeChart =
    ChartFactory.createXYLineChart(
      "Real-time Infected",
      "",
      "",
      dataset,
      PlotOrientation.HORIZONTAL,
      false,
      false,
      false
    )
  private val lineChartPanel: ChartPanel = new MyChartPanel(lineChart)
  private val infectedExtractor: DataExtractor[Int] = Infected()

  override def init(): Task[Unit] =
    for
      _ <- io(lineChart.getTitle.setFont(new Font("Times New Roman", Font.BOLD, 16)))
      _ <- io(lineChart.getPlot.setBackgroundPaint(Color.WHITE))
      _ <- io(lineChart.setBackgroundPaint(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(lineChartPanel.setBackground(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(lineChartPanel.setVisible(true))
      _ <- io(series.add(0, 0))
    yield ()

  override def update(env: Environment): Task[Unit] =
    for
      _ <- io {
        series.addOrUpdate(infectedExtractor.extractData(env), env.time.toHours)
      }
    yield ()

  override def chart: JFreeChart = lineChart
  override def chartPanel: ChartPanel = lineChartPanel

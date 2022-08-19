package it.unibo.pps.jvm.boundary.gui.panel.charts

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.boundary.exporter.Extractors.{DataExtractor, Deaths, Sick, Days}
import it.unibo.pps.boundary.exporter.StatisticalData.Stats
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.Values.SimulationColor
import it.unibo.pps.jvm.boundary.gui.panel.charts.Charts.{Chart, MyChartPanel}
import monix.eval.Task
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.statistics.HistogramDataset
import org.jfree.data.xy.{DefaultXYDataset, XYBarDataset, XYDataset}

import java.awt.{Color, Font}

class BarChart extends Chart:
  private val dataset: DefaultCategoryDataset = new DefaultCategoryDataset()
  private val barChart: JFreeChart =
    ChartFactory.createBarChart("Deads, Cured and Sick Entities", "", "", dataset)
  private val barChartPanel: ChartPanel = new MyChartPanel(barChart)
  private val deathsExtractor: DataExtractor[Int] = Deaths()
  private val sickExtractor: DataExtractor[Int] = Sick()
  private val daysExtractors: DataExtractor[Long] = Days()

  override def init(): Task[Unit] =
    for
      _ <- io(barChart.getTitle.setFont(new Font("Times New Roman", Font.BOLD, 16)))
      _ <- io(barChart.getPlot.setBackgroundPaint(Color.WHITE))
      _ <- io(barChart.setBackgroundPaint(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(barChartPanel.setBackground(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(barChartPanel.setVisible(true))
    yield ()

  override def update(env: Environment): Task[Unit] =
    for
      _ <- io {
        if !dataset.getColumnKeys.contains(daysExtractors.extractData(env)) then
          dataset.addValue(deathsExtractor.extractData(env), deathsExtractor.name, daysExtractors.extractData(env))
          dataset.addValue(sickExtractor.extractData(env), sickExtractor.name, daysExtractors.extractData(env))
        else
          dataset.setValue(deathsExtractor.extractData(env), deathsExtractor.name, daysExtractors.extractData(env))
          dataset.setValue(sickExtractor.extractData(env), sickExtractor.name, daysExtractors.extractData(env))
      }
    yield ()

  override def chart: JFreeChart = barChart
  override def chartPanel: ChartPanel = barChartPanel

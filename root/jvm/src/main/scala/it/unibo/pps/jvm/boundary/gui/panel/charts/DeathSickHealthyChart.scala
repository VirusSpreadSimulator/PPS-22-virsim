package it.unibo.pps.jvm.boundary.gui.panel.charts

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.control.loader.extractor.EntitiesStats.{Deaths, Sick, Healthy}
import it.unibo.pps.control.loader.extractor.EnvironmentStats.Hours
import it.unibo.pps.control.loader.extractor.StatisticalData.Stats
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.gui.Values.SimulationColor
import it.unibo.pps.jvm.boundary.gui.panel.charts.Charts.{Chart, MyChartPanel}
import monix.eval.Task
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.statistics.HistogramDataset
import org.jfree.data.xy.{DefaultXYDataset, XYBarDataset, XYDataset}

import java.awt.{Color, Font}

class DeathSickHealthyChart extends Chart:
  private val dataset: DefaultCategoryDataset = new DefaultCategoryDataset()
  private val barChart: JFreeChart =
    ChartFactory.createStackedBarChart("Deads, Cured and Sick Entities", "", "", dataset)
  private val barChartPanel: ChartPanel = new MyChartPanel(barChart)
  private val deathsExtractor: DataExtractor[Int] = Deaths()
  private val sickExtractor: DataExtractor[Int] = Sick()
  private val healthyExtractor: DataExtractor[Int] = Healthy()
  private val hoursExtractor: DataExtractor[Long] = Hours()

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
        if !dataset.getColumnKeys.contains(hoursExtractor.extractData(env)) then
          dataset.addValue(deathsExtractor.extractData(env), deathsExtractor.name, hoursExtractor.extractData(env))
          dataset.addValue(sickExtractor.extractData(env), sickExtractor.name, hoursExtractor.extractData(env))
          dataset.addValue(healthyExtractor.extractData(env), healthyExtractor.name, hoursExtractor.extractData(env))
        else
          dataset.setValue(deathsExtractor.extractData(env), deathsExtractor.name, hoursExtractor.extractData(env))
          dataset.setValue(sickExtractor.extractData(env), sickExtractor.name, hoursExtractor.extractData(env))
          dataset.setValue(healthyExtractor.extractData(env), healthyExtractor.name, hoursExtractor.extractData(env))
      }
    yield ()

  override def chart: JFreeChart = barChart
  override def chartPanel: ChartPanel = barChartPanel

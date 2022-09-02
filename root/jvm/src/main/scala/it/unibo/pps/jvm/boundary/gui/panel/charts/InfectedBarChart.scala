package it.unibo.pps.jvm.boundary.gui.panel.charts

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.control.loader.extractor.EntitiesStats.Infected
import it.unibo.pps.control.loader.extractor.EnvironmentStats.Hours
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.control.loader.extractor.Extractor.*
import it.unibo.pps.control.loader.extractor.StatisticalData.Stats
import it.unibo.pps.jvm.boundary.gui.Values.SimulationColor
import it.unibo.pps.jvm.boundary.gui.panel.charts.Charts.{Chart, MyChartPanel}
import monix.eval.Task
import org.jfree.chart.plot.{CategoryPlot, PlotOrientation}
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}

import java.awt.{Color, Font}

/** The chart that display the infected entities inside the environment. */
class InfectedBarChart extends Chart:
  private val dataset: DefaultCategoryDataset = new DefaultCategoryDataset()
  private val infectedBarChart: JFreeChart =
    ChartFactory.createBarChart("Infected per Hour", "", "", dataset)
  private val infectedBarChartPanel: ChartPanel = new MyChartPanel(infectedBarChart)
  private val infectedExtractor: DataExtractor[Int] = Infected()
  private val hoursExtractor: DataExtractor[Long] = Hours()

  override def init(): Task[Unit] =
    for
      _ <- io(infectedBarChart.getTitle.setFont(new Font("Times New Roman", Font.BOLD, 16)))
      _ <- io(infectedBarChart.getPlot.setBackgroundPaint(Color.WHITE))
      _ <- io(infectedBarChart.getPlot.asInstanceOf[CategoryPlot].getRenderer().setSeriesPaint(0, Color.CYAN))
      _ <- io(infectedBarChart.setBackgroundPaint(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(infectedBarChartPanel.setBackground(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(infectedBarChartPanel.setVisible(true))
    yield ()

  override def update(env: Environment): Task[Unit] =
    for
      _ <- io {
        if !dataset.getColumnKeys.contains(hoursExtractor.extractData(env)) then
          dataset.addValue(infectedExtractor.extractData(env), infectedExtractor.name, hoursExtractor.extractData(env))
        else
          dataset.setValue(infectedExtractor.extractData(env), infectedExtractor.name, hoursExtractor.extractData(env))
      }
    yield ()

  override def chart: JFreeChart = infectedBarChart
  override def chartPanel: ChartPanel = infectedBarChartPanel

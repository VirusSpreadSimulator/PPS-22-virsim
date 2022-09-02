package it.unibo.pps.jvm.boundary.gui.panel.charts

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.control.loader.extractor.HospitalStats.{Hospitalized, HospitalFreeSeats}
import it.unibo.pps.control.loader.extractor.StatisticalData.Stats
import it.unibo.pps.control.loader.extractor.StatisticalData.given
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.gui.Values.SimulationColor
import it.unibo.pps.jvm.boundary.gui.panel.charts.Charts.{Chart, MyChartPanel}
import monix.eval.Task
import org.jfree.chart.labels.BubbleXYItemLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}
import org.jfree.data.general.DefaultPieDataset

import java.awt.{Color, Font}
import scala.reflect.ClassTag.Nothing

/** The chart that display the pressure of hospitals inside the environment. */
class PieChart extends Chart:

  private val dataset = new DefaultPieDataset[String]()
  private val pieChart: JFreeChart =
    ChartFactory.createPieChart("Hospitals Pressure", dataset, true, true, false)
  private val pieChartPanel: ChartPanel = new MyChartPanel(pieChart)
  private val extractors: List[DataExtractor[Int]] = List(Hospitalized(), HospitalFreeSeats())

  override def init(): Task[Unit] =
    for
      _ <- io(pieChart.getTitle.setFont(new Font("Times New Roman", Font.BOLD, 16)))
      _ <- io(pieChart.getPlot.asInstanceOf[PiePlot[String]].setSimpleLabels(true))
      _ <- io(pieChart.getPlot.asInstanceOf[PiePlot[String]].setSectionPaint(extractors.head.name, Color.RED))
      _ <- io(pieChart.getPlot.asInstanceOf[PiePlot[String]].setSectionPaint(extractors(1).name, Color.ORANGE))
      _ <- io(pieChart.getPlot.setBackgroundPaint(Color.WHITE))
      _ <- io(pieChart.setBackgroundPaint(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(pieChartPanel.setBackground(SimulationColor.BACKGROUND_CHART_PANEL_COLOR))
      _ <- io(pieChartPanel.setVisible(true))
    yield ()

  override def update(env: Environment): Task[Unit] =
    for
      _ <- io {
        for extractor <- extractors do dataset.setValue(extractor.name, extractor.extractData(env))
      }
    yield ()

  override def chart: JFreeChart = pieChart
  override def chartPanel: ChartPanel = pieChartPanel

package it.unibo.pps.jvm.boundary.gui

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.jvm.boundary.Utils
import it.unibo.pps.jvm.boundary.gui.SimulationGUI
import it.unibo.pps.jvm.boundary.gui.panel.SimulationPanel
import it.unibo.pps.jvm.boundary.gui.panel.ChartPanel
import monix.reactive.Observable
import monix.eval.Task

import java.awt.{BorderLayout, GridLayout}
import javax.swing.{BoxLayout, JFrame, JPanel, JScrollPane, JSplitPane, ScrollPaneConstants, SwingUtilities}

trait SimulationGUI:
  def init(): Task[Unit]
  def render(): Task[Unit]
  def events(): Observable[Event]

object SimulationGUI: //todo: group all the magic number in Values
  def apply(width: Int = 800, height: Int = 700, title: String = "Virsim"): SimulationGUI =
    SimulationGUIImpl(width + 20, height + 20, title)
  private class SimulationGUIImpl(width: Int, height: Int, title: String) extends SimulationGUI:
    import Utils.given

    private lazy val simulationPanel = SimulationPanel()
    private lazy val chartPanel = ChartPanel()

    private lazy val container: Task[JFrame] =
      for
        frame <- io(JFrame(title))
        _ <- io(frame.setMinimumSize((width, height)))
        _ <- io(frame.setSize(width, height))
        _ <- io(frame.setLocationRelativeTo(null))
      yield frame

    private lazy val topPanel: Task[JSplitPane] =
      for
        _ <- io(simulationPanel.setMinimumSize((500, 500)))
        _ <- io(chartPanel.setMinimumSize((300, 500)))
        split <- io(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, simulationPanel, chartPanel))
        _ <- io(split.setResizeWeight(0.5))
        _ <- io(split.setOneTouchExpandable(true))
        _ <- io(split.setContinuousLayout(true))
      yield split

    private lazy val bottomPanel: Task[JPanel] =
      for
        panel <- io(JPanel())
        panelLM <- io(GridLayout(1, 5))
        _ <- io(panel.setLayout(panelLM))
      yield panel

    private lazy val mainPanel: Task[JSplitPane] =
      for
        topP <- topPanel
        _ <- io(topP.setMinimumSize((800, 500)))
        bottomP <- bottomPanel
        _ <- io(bottomP.setMinimumSize((800, 200)))
        split <- io(JSplitPane(JSplitPane.VERTICAL_SPLIT, topP, bottomP))
        _ <- io(split.setResizeWeight(0.5))
        _ <- io(split.setOneTouchExpandable(true))
        _ <- io(split.setContinuousLayout(true))
      yield split

    override def init(): Task[Unit] =
      for
        frame <- container.asyncBoundary(Utils.swingScheduler)
        mainP <- mainPanel
        _ <- io(mainP.setOpaque(true))
        _ <- io(frame.add(mainP))
        _ <- io(frame.pack())
        _ <- io(frame.setVisible(true))
      yield ()

    override def render(): Task[Unit] =
      for
        _ <- Task.pure {}.asyncBoundary(Utils.swingScheduler)
        _ <- io(simulationPanel.updateAndDisplay())
        _ <- io(chartPanel.updateAndDisplay())
        _ <- io(println(chartPanel.getSize().width))
      yield ()

    override def events(): Observable[Event] = ???

  @main def main(): Unit =
    import monix.execution.Scheduler
    import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}
    given Scheduler = monix.execution.Scheduler.global
    val gui = SimulationGUI()
    gui.init().runSyncUnsafe()
    (for
      _ <- Task.sleep(FiniteDuration(100, MILLISECONDS))
      _ <- gui.render()
      _ <- Task.shift
    yield gui).loopForever.runAsyncAndForget

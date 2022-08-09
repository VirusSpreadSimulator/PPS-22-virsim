package it.unibo.pps.jvm.boundary.gui

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.jvm.boundary.Utils
import it.unibo.pps.jvm.boundary.Values.{Dimension, Text}
import it.unibo.pps.jvm.boundary.gui.SimulationGUI
import it.unibo.pps.jvm.boundary.gui.panel.SimulationPanel
import it.unibo.pps.jvm.boundary.gui.panel.ChartPanel
import it.unibo.pps.jvm.boundary.gui.panel.BottomPanels.{CommandPanel, DynamicConfigPanel}
import monix.reactive.Observable
import monix.eval.Task

import java.awt.{BorderLayout, GridLayout}
import javax.swing.{
  BoxLayout,
  JFrame,
  JPanel,
  JScrollPane,
  JSplitPane,
  ScrollPaneConstants,
  SwingUtilities,
  WindowConstants
}

/** Interface that describe the user interface for the simulation */
trait SimulationGUI:
  /** Init the simulation user interface
    * @return
    *   the task
    */
  def init(): Task[Unit]
  /** Render the new state of the simulation on the user interface
    * @return
    */
  def render(): Task[Unit]
  /** Obtain the observable that emit all the events of the user interface
    * @return
    *   the observable
    */
  def events(): Observable[Event]

object SimulationGUI:
  /** Factory to create a simulation GUI
    * @param width
    *   the width of the window
    * @param height
    *   the height of the window
    * @param title
    *   the title of the window
    * @return
    *   the Simulation GUI.
    */
  def apply(
      width: Int = Dimension.SIMULATIONGUI_WIDTH,
      height: Int = Dimension.SIMULATIONGUI_HEIGHT,
      title: String = Text.SIMULATOR_NAME_SHORT
  ): SimulationGUI =
    SimulationGUIImpl(width, height, title)
  private class SimulationGUIImpl(width: Int, height: Int, title: String) extends SimulationGUI:
    import Utils.given

    // Top panels
    private lazy val simulationPanel = SimulationPanel()
    private lazy val chartPanel = ChartPanel()
    // Bottom panels
    private lazy val commandPanel = CommandPanel()
    private lazy val dynamicConfigPanel = DynamicConfigPanel()

    private lazy val container: Task[JFrame] =
      for
        frame <- io(JFrame(title))
        _ <- io(frame.setMinimumSize((width, height)))
        _ <- io(frame.setSize(width, height))
        _ <- io(frame.setLocationRelativeTo(null))
      yield frame

    private lazy val topPanel: Task[JSplitPane] =
      for
        _ <- io(simulationPanel.setMinimumSize(Dimension.SIMULATION_PANEL_MIN_DIMENSION))
        _ <- io(chartPanel.setMinimumSize(Dimension.CHART_PANEL_MIN_DIMENSION))
        split <- io(JSplitPane(JSplitPane.HORIZONTAL_SPLIT, simulationPanel, chartPanel))
        _ <- io(split.setResizeWeight(1))
        _ <- io(split.setOneTouchExpandable(true))
        _ <- io(split.setContinuousLayout(true))
      yield split

    private lazy val bottomPanel: Task[JPanel] =
      for
        panel <- io(JPanel())
        panelLM <- io(GridLayout(1, 5))
        _ <- io(panel.setLayout(panelLM))
        panels = Seq(commandPanel, dynamicConfigPanel)
        _ <- io(for p <- panels do panel.add(p))
        _ <- io(for p <- panels do p.display())
      yield panel

    private lazy val mainPanel: Task[JSplitPane] =
      for
        topP <- topPanel
        _ <- io(topP.setMinimumSize(Dimension.SIMULATION_GUI_TOP_DIMENSION))
        bottomP <- bottomPanel
        _ <- io(bottomP.setMinimumSize(Dimension.SIMULATION_GUI_BOTTOM_DIMENSION))
        split <- io(JSplitPane(JSplitPane.VERTICAL_SPLIT, topP, bottomP))
        _ <- io(split.setResizeWeight(1))
        _ <- io(split.setOneTouchExpandable(true))
        _ <- io(split.setContinuousLayout(true))
      yield split

    override def init(): Task[Unit] =
      for
        frame <- container.asyncBoundary(Utils.swingScheduler)
        _ <- io(frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))
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
      yield ()

    override def events(): Observable[Event] =
      Observable
        .fromIterable(Seq(commandPanel, dynamicConfigPanel))
        .flatMap(_.events)

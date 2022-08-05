package it.unibo.pps.jvm.boundary.gui

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.jvm.boundary.Utils
import it.unibo.pps.jvm.boundary.gui.SimulationGUI
import it.unibo.pps.jvm.boundary.gui.panel.SimulationPanel
import monix.reactive.Observable
import monix.eval.Task

import java.awt.BorderLayout
import javax.swing.{BoxLayout, JFrame, JPanel, JScrollPane, ScrollPaneConstants}

trait SimulationGUI:
  def init(): Task[Unit]
  def render(): Task[Unit]
  def events(): Observable[Event]

object SimulationGUI:
  def apply(width: Int = 1024, height: Int = 768, title: String = "Virsim"): SimulationGUI =
    SimulationGUIImpl(width, height, title)
  private class SimulationGUIImpl(width: Int, height: Int, title: String) extends SimulationGUI:
    import Utils.given
    private lazy val simPanel = SimulationPanel()
    private lazy val chartPanel = JPanel()

    private lazy val container: Task[JFrame] =
      for
        frame <- io(JFrame(title))
        _ <- io(frame.setMinimumSize((width, height)))
        _ <- io(frame.setSize(width, height))
        _ <- io(frame.setLocationRelativeTo(null))
      yield frame

    private lazy val topPanel: Task[JPanel] =
      for panel <- io(JPanel())
      yield panel

    private lazy val bottomPanel: Task[JPanel] =
      for panel <- io(JPanel())
      yield panel

    private lazy val mainPanel: Task[JPanel] =
      for mainPanel <- io(JPanel())
//        _ <- io(mainPanel.add(simPanel, BorderLayout.CENTER))
      yield mainPanel

    override def init(): Task[Unit] =
      for
        frame <- container.asyncBoundary(Utils.swingScheduler)
        mainP <- mainPanel
        _ <- io(frame.setContentPane(mainP))
        _ <- io(frame.setVisible(true))
      yield ()

    override def render() = io(simPanel.display())

    override def events(): Observable[Event] = ???

  @main def main(): Unit =
    import monix.execution.Scheduler
    given Scheduler = monix.execution.Scheduler.global
    (for
      gui <- Task(SimulationGUI())
      _ <- gui.init()
      _ <- gui.render()
    yield gui).runAsyncAndForget

package it.unibo.pps.jvm.boundary

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import it.unibo.pps.jvm.boundary.component.MonadButton
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

import java.awt.event.ActionEvent
import javax.swing.{JButton, JFrame, SwingUtilities}
import scala.concurrent.ExecutionContext

trait GUI:
  def init(): Task[Unit]
  def render(i: Int): Task[Unit]
  def events(): Observable[Event]

object GUI:
  def apply(width: Int = 200, height: Int = 150, title: String = "Virsim"): GUI =
    GUIImpl(width, height, title)
  private class GUIImpl(width: Int, height: Int, title: String) extends GUI:
    private lazy val container: Task[JFrame] =
      for
        frame <- io(JFrame(title))
        _ <- io(frame.setSize(100, 100))
      yield frame

    private lazy val renderBtns: Seq[MonadButton] =
      Seq(
        MonadButton("click me!", Hit(100))
      )

    override def events(): Observable[Event] = Observable
      .fromIterable(renderBtns)
      .flatMap(_.events)

    override def init(): Task[Unit] =
      for
        frame <- container.asyncBoundary(SwingConfiguration.swingScheduler)
        _ <- io(renderBtns.map(_.button).foreach(frame.add))
        _ <- io(frame.setVisible(true))
      yield ()

    override def render(i: Int): Task[Unit] =
      for
        _ <- Task.pure {}.asyncBoundary(SwingConfiguration.swingScheduler)
        _ <- Task(println(i))
      yield ()

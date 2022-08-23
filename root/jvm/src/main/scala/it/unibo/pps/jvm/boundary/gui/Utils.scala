package it.unibo.pps.jvm.boundary.gui

import monix.execution.Scheduler

import java.awt.Dimension
import javax.swing.SwingUtilities
import scala.concurrent.ExecutionContext

// todo: refactor
object Utils:
  given Conversion[(Int, Int), Dimension] with
    override def apply(x: (Int, Int)): Dimension = Dimension(x._1, x._2)

  val swingScheduler: Scheduler = Scheduler.apply(new ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeLater(runnable)
    override def reportFailure(cause: Throwable): Unit = {} // todo: technical debt
  })

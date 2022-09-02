package it.unibo.pps.jvm.boundary.gui

import monix.execution.Scheduler
import java.awt.Dimension
import javax.swing.SwingUtilities
import scala.concurrent.ExecutionContext

/** Module that contains some utils useful in this gui boundary. */
object Utils:
  /** Conversion from a tuple of integer to java awt [[Dimension]]. */
  given Conversion[(Int, Int), Dimension] with
    override def apply(x: (Int, Int)): Dimension = Dimension(x._1, x._2)

  /** Swing Scheduler used to execute ui-related computations. */
  val swingScheduler: Scheduler = Scheduler.apply(new ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeLater(runnable)
    /** failure, no exception. */
    override def reportFailure(cause: Throwable): Unit = {}
  })

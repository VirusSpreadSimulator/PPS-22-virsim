package it.unibo.pps.jvm.boundary

import monix.execution.Scheduler
import javax.swing.SwingUtilities
import scala.concurrent.ExecutionContext

// todo: refactor
object SwingConfiguration:
  val swingScheduler: Scheduler = Scheduler.apply(new ExecutionContext {
    override def execute(runnable: Runnable): Unit = SwingUtilities.invokeLater(runnable)
    override def reportFailure(cause: Throwable): Unit = {} // todo: technical debt
  })

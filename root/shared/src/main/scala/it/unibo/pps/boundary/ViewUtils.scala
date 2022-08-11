package it.unibo.pps.boundary

import monix.eval.Task
import monix.execution.Scheduler

import javax.swing.SwingUtilities
import scala.concurrent.ExecutionContext

object ViewUtils:
  // A facade on Task with a terminology more related to UI.
  def io[A](computation: => A): Task[A] = Task(computation)

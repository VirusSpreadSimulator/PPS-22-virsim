package it.unibo.pps.boundary

import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import monix.eval.Task
import monix.execution.Scheduler

import javax.swing.SwingUtilities
import scala.concurrent.ExecutionContext

/** Module that contains some utils useful for view boundaries */
object ViewUtils:
  /** A facade on Task with a terminology more related to UI.
    * @param computation
    *   the computation to perform in a lazy way.
    * @return
    *   the task.
    */
  def io[A](computation: => A): Task[A] = Task(computation)
  /** Utility useful to scale a coordinate based on the current scaling of the view.
    * @param coordinate
    *   the coordinate to scale.
    * @param scale
    *   the current scale.
    * @return
    *   a scaled coordinate.
    */
  def scaleToView(coordinate: Int, scale: Int): Int = coordinate * scale
  /** Case class that is useful in gui to display stats associated to its label
    * @param label
    *   the label in which display the stat
    * @param extractor
    *   the extractor to use
    * @tparam E
    *   the type of the label
    * @tparam A
    *   the type of data to extract
    */
  case class StatsDisplayer[E, A](label: E, extractor: DataExtractor[A])

package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.reactive.Observable
import monix.eval.Task
import javax.swing.JPanel

/** This module contains the interfaces that describe the different types of panel that are present in the simulation
  * gui.
  */
object Panels:
  /** DisplayblePanel represents a panel that is only updated once, so it needs only to be displayed once. All the
    * operations are represented as lazy monix Tasks, in order to express in a better way the computation.
    */
  trait DisplayblePanel extends JPanel:
    /** Init the panel.
      * @return
      *   the task
      */
    def init(): Task[Unit]
    /** Stop the panel.
      * @return
      *   the task
      */
    def stop(): Task[Unit] = Task.pure {}

  /** UpdateblePanel represents a panel that can be updated during the simulation. All the operations are represented as
    * lazy monix Tasks, in order to express in a better way the computation.
    */
  trait UpdateblePanel extends JPanel with DisplayblePanel:
    /** Method to update the panel.
      * @param env
      *   the environment to render
      * @return
      *   the task
      */
    def update(env: Environment): Task[Unit]

  /** EventablePanel represent a panel that is able to emit events. It's represented as a self-type on the JPanel */
  trait EventablePanel:
    panel: JPanel =>
    /** Method to obtain the events that the panel emit.
      * @return
      *   the observable
      */
    def events: Observable[Event]

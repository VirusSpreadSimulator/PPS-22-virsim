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
  /** DisplayblePanel represent a panel that is only updated once, so it needs only to be displayed once All the
    * operations are represented as lazy monix Tasks, in order to express in a better way the computation.
    */
  trait DisplayblePanel extends JPanel:
    def init(): Task[Unit]

  /** UpdateblePanel represent a panel that can be updated during the simulation. All the operations are represented as
    * lazy monix Tasks, in order to express in a better way the computation.
    */
  trait UpdateblePanel extends JPanel with DisplayblePanel:
    def update(env: Environment): Task[Unit]

  /** EventablePanel represent a panel that is able to emit events. It's represented as a self-type on the JPanel */
  trait EventablePanel:
    panel: JPanel =>
    def events: Observable[Event]

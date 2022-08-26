package it.unibo.pps.boundary.component.panel

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import monix.reactive.Observable

/** This module contains the interfaces that describe the different types of panel that are present in the simulation
  * gui. It is a concept that is platform independent.
  */
object Panels:
  /** BasePanel represent a panel that is only updated once, so it needs only to be displayed once. All the operations
    * are represented as lazy monix Tasks, in order to express in a better way the computation.
    */
  trait BasePanel:
    /** Init the panel.
      * @return
      *   the task
      */
    def init(): Task[Unit]
    /** It's called when the simulation is stopped, useful for closing operations.
      * @return
      *   the task
      */
    def stop(): Task[Unit] = Task.pure {}

  /** UpdatablePanel represent a panel that can be updated during the simulation. All the operations are represented as
    * lazy monix Tasks, in order to express in a better way the computation.
    */
  trait UpdatablePanel extends BasePanel:
    /** Called to update the panel with the new [[Environment]].
      * @param newEnv
      *   the new environment
      * @return
      *   the task
      */
    def update(newEnv: Environment): Task[Unit]

  /** EventablePanel represent a panel that is able to emit events. */
  trait EventablePanel:
    panel: BasePanel =>
    /** Method to obtain the events generated by the panel.
      * @return
      *   the observable of events
      */
    def events: Observable[Event]

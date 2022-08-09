package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.boundary.component.Events.Event
import monix.reactive.Observable

import javax.swing.JPanel

/** This module contains the interfaces that describe the different types of panel that are present in the simulation
  * gui
  */
object Panels:
  /** UpdateblePanel represent a panel that can be updated during the simulation. */
  trait UpdateblePanel extends JPanel:
    def updateAndDisplay(): Unit // todo: pass the env

  /** DisplayblePanel represent a panel that is only updated once, so it needs only to be displayed once */
  trait DisplayblePanel extends JPanel:
    def display(): Unit

  /** EventablePanel represent a panel that is able to emit events. It's represented as a self-type on the JPanel */
  trait EventablePanel:
    panel: JPanel =>
    def events: Observable[Event]

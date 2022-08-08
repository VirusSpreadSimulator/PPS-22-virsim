package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.boundary.component.Events.Event
import monix.reactive.Observable

import javax.swing.JPanel

object Panels:
  trait UpdateblePanel extends JPanel:
    def updateAndDisplay(): Unit // todo: pass the env

  trait DisplayblePanel extends JPanel:
    def display(): Unit

  trait EventablePanel:
    def events: Observable[Event]

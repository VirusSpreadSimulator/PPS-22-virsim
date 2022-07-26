package it.unibo.pps.jvm.boundary

import it.unibo.pps.boundary.BoundaryModule.Boundary
import it.unibo.pps.boundary.component.Events.Event
import monix.reactive.Observable

object GUIModule:
  trait Provider:
    val gui: Boundary
  trait Component:
    class GUIBoundaryImpl extends Boundary:
      private val guiSwing = GUI()
      override def init() = guiSwing.init()
      override def render(i: Int) = guiSwing.render(i)
      override def events(): Observable[Event] = guiSwing.events()
  trait Interface extends Provider with Component

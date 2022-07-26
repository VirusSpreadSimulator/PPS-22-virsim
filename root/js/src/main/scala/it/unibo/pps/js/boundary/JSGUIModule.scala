package it.unibo.pps.js.boundary

import it.unibo.pps.boundary.BoundaryModule.Boundary
import it.unibo.pps.boundary.component.Events.Event
import monix.reactive.Observable

object JSGUIModule:
  trait Provider:
    val jsGui: Boundary
  trait Component:
    class JSGUIBoundaryImpl extends Boundary:
      private val guiJs = JSGUI()
      override def init() = guiJs.init()
      override def render(i: Int) = guiJs.render(i)
      override def events(): Observable[Event] = guiJs.events()
  trait Interface extends Provider with Component

package it.unibo.pps.boundary.component

import it.unibo.pps.boundary.component.Events.Event
import monix.reactive.Observable

trait EventSource:
  def events: Observable[Event]

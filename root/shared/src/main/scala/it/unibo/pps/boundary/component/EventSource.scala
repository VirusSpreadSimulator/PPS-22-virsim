package it.unibo.pps.boundary.component

import it.unibo.pps.boundary.component.Events.Event
import monix.reactive.Observable

/** Trait that represent an EventSource, so a source that can emit events of type [[Event]] */
trait EventSource:
  /** @return an observable that allow to receive all the [[Event]] emitted by the [[EventSource]] */
  def events: Observable[Event]

package it.unibo.pps.boundary
import monix.eval.Task
import monix.reactive.Observable
import component.Events.Event

object BoundaryModule:
  trait Boundary:
    def init(): Task[Unit]
    def render(i: Int): Task[Unit]
    def events(): Observable[Event]
  trait Provider:
    val boundaries: Seq[Boundary]
  trait Interface extends Provider

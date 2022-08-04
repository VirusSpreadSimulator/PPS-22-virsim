package it.unibo.pps.boundary
import monix.eval.Task
import monix.reactive.Observable
import component.Events.Event

import java.nio.file.Path

object BoundaryModule:
  trait Boundary:
    def init(): Task[Unit]
    def start(): Task[Unit]
    def render(i: Int): Task[Unit]
    def events(): Observable[Event]
  trait ConfigBoundary:
    def config(): Task[Path]
    def error(): Task[Unit]
  trait Provider:
    val boundaries: Seq[Boundary]
  trait Interface extends Provider

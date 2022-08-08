package it.unibo.pps.boundary
import monix.eval.Task
import monix.reactive.Observable
import component.Events.Event
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError

import java.nio.file.Path

object BoundaryModule:
  trait Boundary:
    def init(): Task[Unit]
    def start(): Task[Unit]
    def render(i: Int): Task[Unit]
    def events(): Observable[Event]
  trait ConfigBoundary extends Boundary:
    def config(): Task[Path]
    def error(error: ConfigurationError): Task[Unit]
  trait Provider:
    val boundaries: Seq[Boundary]
    val configBoundary: ConfigBoundary
  trait Interface extends Provider

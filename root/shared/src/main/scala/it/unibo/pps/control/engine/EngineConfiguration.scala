package it.unibo.pps.control.engine

import monix.execution.Scheduler

import scala.concurrent.duration.FiniteDuration

object EngineConfiguration:
  trait SimulationConfiguration:
    def maxEventPerIteration: Int
    def tickTime: FiniteDuration

  given Scheduler = monix.execution.Scheduler.global
  given SimulationConfiguration with
    override val maxEventPerIteration = 3
    override val tickTime = 100.millis

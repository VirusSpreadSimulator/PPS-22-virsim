package it.unibo.pps.control.engine

import monix.execution.Scheduler

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.DurationInt

object EngineConfiguration:
  trait SimulationConfig:
    def maxEventPerIteration: Int
    def tickTime: FiniteDuration

  given Scheduler = monix.execution.Scheduler.global
  given SimulationConfig with
    override val maxEventPerIteration = 3
    override val tickTime = 100.millis

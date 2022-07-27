package it.unibo.pps.control

import monix.execution.Scheduler
import scala.concurrent.duration.FiniteDuration
import concurrent.duration.DurationInt

object EngineConfiguration:
  trait SimulationConfiguration:
    def maxEventPerIteration: Int
    def tickTime: FiniteDuration

  given Scheduler = monix.execution.Scheduler.global
  given SimulationConfiguration with
    override val maxEventPerIteration = 3
    override val tickTime = 100.millis

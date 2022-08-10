package it.unibo.pps.control.engine.config

import it.unibo.pps.control.engine.config.Configurations.EngineSpeed
import monix.execution.Scheduler

/** It is the module that contains the configuration of the simulation */
object EngineConfiguration:
  /** [[SimulationConfig]] represent the configuration needed by the engine to be able to work */
  trait SimulationConfig:
    /** The engine process events that came from the boundary at every iteration. This value represent the number of
      * events that are processes from the queue at every tick. Consider that process an higher number of events every
      * tick means a slower processing for each tick, but a lower value means that event may be considered after some
      * time.
      * @return
      *   the maximum number of events to consider each iteration
      */
    def maxEventPerIteration: Int
    /** The engine speed
      * @return
      *   the engine speed
      */
    def engineSpeed: EngineSpeed
    /** Setter for the engine speed. This because the speed can be modified by the user at runtime.
      * @param speed
      *   the [[EngineSpeed]]
      */
    def engineSpeed_=(speed: EngineSpeed): Unit

  given Scheduler = monix.execution.Scheduler.global
  given SimulationConfig with
    override val maxEventPerIteration = 3
    override var engineSpeed = EngineSpeed.NORMAL

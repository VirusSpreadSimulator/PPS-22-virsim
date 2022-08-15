package it.unibo.pps.control.engine.behaviouralLogics

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.engine.config.Configurations.EngineStatus
import it.unibo.pps.control.engine.config.EngineConfiguration.SimulationConfig
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import monocle.syntax.all.*

/** Module that wrap all the logic types that are needed to update the simulation [[Environment]] */
object Logic:
  /** Update logic represent a generic logic that is performed at each iteration. It takes the current environment and
    * return a task that represent the computation done on that environment
    */
  type UpdateLogic = Environment => Task[Environment]

  object UpdateLogic:
    /** Identity logic
      * @return
      *   the logic
      */
    def identity: UpdateLogic = Task(_)
    /** Handle the update of the time in the environment
      * @return
      *   the logic
      */
    def logicTimeUpdate: UpdateLogic = env => Task(env.update(time = env.time + 1))
    /** Handle the termination of the simulation
      * @param config
      *   the simulation configuration to modify
      * @return
      *   the logic
      */
    def iterationLogic(config: SimulationConfig): UpdateLogic = env =>
      for
        over <- Task(env.time >= TimeStamp() + env.environmentDuration)
        _ <- Task(if over then config.engineStatus = EngineStatus.STOPPED)
      yield env

  /** Update logic represent a logic that is associated to an event. It takes the current environment and return a task
    * that represent the computation done on that environment due to the occur of the event.
    */
  type EventLogic = Environment => Task[Environment]

  object EventLogic:
    import it.unibo.pps.control.engine.config.Configurations.EngineSpeed
    /** Identity logic */
    def identity: EventLogic = Task(_)
    /** Logic to handle the paused state
      * @param config
      *   the configuration to modify
      * @return
      *   the event logic
      */
    def pauseLogic(config: SimulationConfig): EventLogic = env =>
      for _ <- Task(config.engineStatus = EngineStatus.PAUSED)
      yield env
    /** Logic to resume the simulation
      * @param config
      *   the configuration to modify
      * @return
      *   the event logic
      */
    def resumeLogic(config: SimulationConfig): EventLogic = env =>
      for _ <- Task(config.engineStatus = EngineStatus.RUNNING)
      yield env
    /** Logic to handle the stopped state
      * @param config
      *   the configuration to modify
      * @return
      *   the event logic
      */
    def stopLogic(config: SimulationConfig): EventLogic = env =>
      for _ <- Task(config.engineStatus = EngineStatus.STOPPED)
      yield env
    /** Logic to change the simulation speed
      * @param config
      *   the configuration to modify
      * @param engineSpeed
      *   the speed to set
      * @return
      *   the event logic
      */
    def simulationSpeedLogic(config: SimulationConfig, engineSpeed: EngineSpeed): EventLogic = env =>
      for _ <- Task(config.engineSpeed = engineSpeed)
      yield env

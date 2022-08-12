package it.unibo.pps.control.engine.behaviouralLogics

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.engine.config.Configurations.EngineStatus
import it.unibo.pps.control.engine.config.EngineConfiguration.SimulationConfig
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
    /** Identity logic */
    def identity: UpdateLogic = Task(_)

    /** Handle the update of the time in the environment */
    def logicTimeUpdate: UpdateLogic = env => Task(env.update(time = env.time + 1))

  /** Update logic represent a logic that is associated to an event. It takes the current environment and return a task
    * that represent the computation done on that environment due to the occur of the event.
    */
  type EventLogic = Environment => Task[Environment]

  object EventLogic:
    import it.unibo.pps.control.engine.config.Configurations.EngineSpeed
    /** Identity logic */
    def identity: EventLogic = Task(_)
    def pauseLogic(config: SimulationConfig): EventLogic = env =>
      for _ <- Task(config.engineStatus = EngineStatus.PAUSED)
      yield env
    def resumeLogic(config: SimulationConfig): EventLogic = env =>
      for _ <- Task(config.engineStatus = EngineStatus.RUNNING)
      yield env
    def stopLogic(config: SimulationConfig): EventLogic = env =>
      for _ <- Task(config.engineStatus = EngineStatus.STOPPED)
      yield env
    def simulationSpeedLogic(config: SimulationConfig, engineSpeed: EngineSpeed): EventLogic = env =>
      for _ <- Task(config.engineSpeed = engineSpeed)
      yield env

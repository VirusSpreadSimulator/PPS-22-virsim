package it.unibo.pps.control.engine.config

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.engine.logics.Logic.{EventLogic, UpdateLogic}
import it.unibo.pps.control.engine.config.Configurations.{EngineSpeed, EngineStatus}
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
    /** The current [[EngineStatus]]
      * @return
      *   the status
      */
    def engineStatus: EngineStatus
    /** Setter to change the engine status. It is useful to pause, resume and stop the simulation
      * @param status
      *   the [[EngineStatus]] to set
      */
    def engineStatus_=(status: EngineStatus): Unit
    /** The sequence of all the logics that need to be executed every iteration
      * @return
      *   the list of logics
      */
    def logics: Seq[UpdateLogic]
    /** It returns a function that for each event returns the specific logic that is able to handle it.
      * @return
      *   the function that for an event returns the associated logic
      */
    def eventLogics: Event => EventLogic

  given Scheduler = monix.execution.Scheduler.global
  given SimulationConfig with
    override val maxEventPerIteration: Int = 3
    override var engineSpeed: EngineSpeed = EngineSpeed.NORMAL
    override var engineStatus: EngineStatus = EngineStatus.RUNNING
    override val logics: Seq[UpdateLogic] =
      Seq(
        UpdateLogic.entityStateUpdateLogic,
        UpdateLogic.hospitalRecoveryLogic,
        UpdateLogic.hospitalizationLogic,
        UpdateLogic.entityGoalLogic,
        UpdateLogic.movementLogic,
        UpdateLogic.exitStructureLogic,
        UpdateLogic.entranceInStructureLogic,
        UpdateLogic.externalInfectionLogic,
        UpdateLogic.internalInfectionLogic,
        UpdateLogic.logicTimeUpdate,
        UpdateLogic.iterationLogic(this)
      )
    override val eventLogics: Event => EventLogic = _ match
      case Event.Pause => EventLogic.pauseLogic(this)
      case Event.Resume => EventLogic.resumeLogic(this)
      case Event.Stop => EventLogic.stopLogic(this)
      case Event.ChangeSpeed(speed) => EventLogic.simulationSpeedLogic(this, EngineSpeed.fromEvent(speed))
      case Event.SwitchMaskObligation => EventLogic.identity
      case Event.VaccineRound(percentage) => EventLogic.identity
      case Event.SwitchStructure(group) => EventLogic.switchStructureLogic(group)

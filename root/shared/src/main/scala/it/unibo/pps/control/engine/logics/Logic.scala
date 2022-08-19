package it.unibo.pps.control.engine.logics

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.engine.config.Configurations.EngineStatus
import it.unibo.pps.control.engine.config.EngineConfiguration.SimulationConfig
import it.unibo.pps.control.engine.logics.entitygoal.EntityGoalLogic.EntityGoalUpdateLogic
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.control.engine.logics.infection.InfectionLogic.ExternalInfectionLogic
import it.unibo.pps.control.engine.logics.infection.InfectionLogic.InternalInfectionLogic
import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.UpdateEntityStateLogic
import monix.eval.Task
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Groupable}
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, SimulationStructure}
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
    /** Handle the infection in the environment, external to structures
      * @return
      *   the logic
      */
    def externalInfectionLogic: UpdateLogic = ExternalInfectionLogic()
    /** Handle the infection internal to structures
      * @return
      *   the logic
      */
    def internalInfectionLogic: UpdateLogic = InternalInfectionLogic()
    /** Handle the update of the entities state: health, immunity, recovery from virus
      * @return
      *   the logic
      */
    def entityStateUpdateLogic: UpdateLogic = UpdateEntityStateLogic()
    /** Handle the update of the entity goal respect to the period of the day
      * @return
      *   the logic
      */
    def entityGoalLogic: UpdateLogic = EntityGoalUpdateLogic()

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
    /** Logic to switch the opening state of a group of structure
      * @param group
      *   the group to switch
      * @return
      *   the event logic
      */
    def switchStructureLogic(group: String): EventLogic = env =>
      for
        structures <- Task(env.structures)
        structuresToUpdate <- Task {
          structures.select[SimulationStructure with Closable with Groupable].filter(_.group == group)
        }
        updatedStructures = structuresToUpdate.map { struct =>
          struct match
            case generic: GenericBuilding => generic.focus(_.isOpen).modify(!_)
            case _ => struct
        }
      yield env.update(structures = structures -- structuresToUpdate ++ updatedStructures)

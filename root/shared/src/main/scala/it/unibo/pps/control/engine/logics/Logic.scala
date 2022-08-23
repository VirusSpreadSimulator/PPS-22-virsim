package it.unibo.pps.control.engine.logics

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.engine.config.Configurations.EngineStatus
import it.unibo.pps.control.engine.config.EngineConfiguration.SimulationConfig
import it.unibo.pps.control.engine.logics.entitygoal.EntityGoalLogic.EntityGoalUpdateLogic
import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.UpdateEntityStateLogic
import it.unibo.pps.control.engine.logics.entitystate.HospitalRecoveryLogic
import it.unibo.pps.control.engine.logics.entitystate.HospitalizationLogic.HospitalizeEntityLogic
import it.unibo.pps.control.engine.logics.entrance.EntranceLogic
import it.unibo.pps.control.engine.logics.exit.ExitLogic
import it.unibo.pps.control.engine.logics.infection.InfectionLogic.{ExternalInfectionLogic, InternalInfectionLogic}
import it.unibo.pps.control.engine.logics.maskObligation.MaskObligationLogic.SwitchMaskObligationLogic
import it.unibo.pps.control.engine.logics.movement.MovementLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{MAX_VALUES, VirusDefaults}
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Groupable}
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, SimulationStructure}
import it.unibo.pps.entity.structure.entrance.Entrance
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
    /** Handle the movement of entities inside the environment
      * @return
      *   the logic
      */
    def movementLogic: UpdateLogic = MovementLogic()
    /** Handle the recovery of the infected entity at risk
      * @return
      *   the logic
      */
    def hospitalizationLogic: UpdateLogic = HospitalizeEntityLogic()
    /** Handle the entity health recovery from virus inside the hospitals
      * @return
      *   the logic
      */
    def hospitalRecoveryLogic: UpdateLogic = HospitalRecoveryLogic()

    def entranceInStructureLogic: UpdateLogic = EntranceLogic()

    def exitStructureLogic: UpdateLogic = ExitLogic()

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

    /** Logic to increase the immunity of a percentage of entities by vaccinate them.
      * @param percentage
      *   the percentage of entities to vaccinate.
      * @return
      *   the event logic
      */
    def vaccineRound(percentage: Double): EventLogic = env =>
      for
        externalEntities <- Task(env.externalEntities)
        numberOfEntitiesToVaccinate = (externalEntities.size * percentage / 100).toInt
        entitiesToUpdate <- Task(externalEntities.filter(_.immunity < 100).take(numberOfEntitiesToVaccinate))
        updatedEntities <- Task {
          entitiesToUpdate.map(entity =>
            entity
              .focus(_.immunity)
              .replace(Math.min(entity.immunity + VirusDefaults.IMMUNITY_GAIN_VACCINATION, MAX_VALUES.MAX_IMMUNITY))
          )
        }
      yield env.update(externalEntities = externalEntities -- entitiesToUpdate ++ updatedEntities)

    /** Logic to switch the obligation to wear a mask.
      * @return
      *   the event logic
      */
    def switchMaskLogic: EventLogic = SwitchMaskObligationLogic()

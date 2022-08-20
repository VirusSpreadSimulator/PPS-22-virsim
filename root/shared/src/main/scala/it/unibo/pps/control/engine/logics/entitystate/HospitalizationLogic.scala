package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MIN_VALUES
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.structure.Structures.Hospital
import monix.eval.Task

object HospitalizationLogic:
  /** Logic to check if entities need to be hospitalized */
  class HospitalizeEntityLogic extends UpdateLogic:
    override def apply(env: Environment): Task[Environment] =
      for
        extUpdatedEnv <- Task { //Check external entities
          env.externalEntities
            .filter(_.infection.isDefined)
            .filter(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT)
            .foldLeft(env)((oldEnv, infectedAtRisk) => tryToHospitalize(oldEnv, infectedAtRisk).getOrElse(oldEnv))
        }
        updatedEnv <- Task { //Check entities inside structures
          extUpdatedEnv.structures
            .filter(!_.isInstanceOf[Hospital])
            .flatMap(structure =>
              structure.entities
                .map(_.entity)
                .filter(_.infection.isDefined)
                .filter(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT)
                .map(entity => (structure, entity))
            )
            .foldLeft(extUpdatedEnv)((oldEnv, pair) =>
              tryToHospitalize(oldEnv, pair._2)
                .map(e => e.update(structures = e.structures - pair._1 + pair._1.entityExit(pair._2)))
                .getOrElse(oldEnv)
            )
        }
      yield updatedEnv

  private def tryToHospitalize(
      env: Environment,
      entity: SimulationEntity
  ): Option[Environment] =
    for
      hospital <- env.structures.select[Hospital].find(h => h.entities.size < h.capacity)
      hospitalUpdated = hospital.tryToEnter(entity, env.time)
      entered = hospitalUpdated.entities.map(_.entity).contains(entity)
      if entered
    yield env.update(
      externalEntities = env.externalEntities - entity,
      structures = env.structures - hospital + hospitalUpdated
    )

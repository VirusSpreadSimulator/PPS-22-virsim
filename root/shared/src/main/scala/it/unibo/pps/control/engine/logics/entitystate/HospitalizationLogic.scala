package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MIN_VALUES
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import monix.eval.Task

/** Module that contains the hospitalization logic concepts. */
object HospitalizationLogic:
  /** Logic to check if entities need to be hospitalized. */
  class HospitalizeEntityLogic extends UpdateLogic:
    /* Check external entities and check internal entities. The hospitalized entities (rec) must leave the structures */
    override def apply(env: Environment): Task[Environment] =
      for
        extUpdatedEnv <- Task {
          env.externalEntities
            .filter(e => e.infection.isDefined && e.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT)
            .foldLeft(env)((oldEnv, infectedAtRisk) => tryToHospitalize(oldEnv, infectedAtRisk).getOrElse(oldEnv))
        }
        structures <- Task(extUpdatedEnv.structures.filter(!_.isInstanceOf[SimulationStructure with Hospitalization]))
        env <- Task {
          structures
            .flatMap(_.entities.map(_.entity))
            .filter(e => e.infection.isDefined && e.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT)
            .foldLeft(extUpdatedEnv)((previousEnv, infectedAtRisk) =>
              tryToHospitalize(previousEnv, infectedAtRisk).getOrElse(previousEnv)
            )
        }
        rec <- Task(env.structures.select[SimulationStructure with Hospitalization].flatMap(_.entities).map(_.entity))
        updatedStructures <- Task(structures.map(rec.foldLeft(_)((s, e) => s.entityExit(e))))
      yield env.update(structures = env.structures -- structures ++ updatedStructures)

  /** Method to try to hospitalize an entity.
    * @param env
    *   the environment
    * @param entity
    *   the entity to hospitalize
    * @return
    *   Some(env) if the entity can find an hospital, None instead.
    */
  private def tryToHospitalize(
      env: Environment,
      entity: SimulationEntity
  ): Option[Environment] =
    for
      hospital <- env.structures
        .select[SimulationStructure with Hospitalization]
        .find(h => h.entities.size < h.capacity)
      hospitalUpdated = hospital.tryToEnter(entity, env.time)
      entered = hospitalUpdated.entities.map(_.entity).contains(entity)
      if entered
    yield env.update(
      externalEntities = env.externalEntities - entity,
      structures = env.structures - hospital + hospitalUpdated
    )

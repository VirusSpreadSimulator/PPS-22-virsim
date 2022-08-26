package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.control.loader.configuration.SimulationDefaults.StructuresDefault
import monix.eval.Task
import monocle.syntax.all.*

/** Logic to handle the entity recovery in hospitals. */
class HospitalRecoveryLogic extends UpdateLogic:
  override def apply(env: Environment): Task[Environment] = for
    structures <- Task(env.structures)
    hospitals <- Task(structures.select[SimulationStructure with Hospitalization])
    hospitalsUpdated <- Task {
      hospitals.map(h =>
        h.updateEntitiesInside { e =>
          Some(e)
            .filter(_.infection.isDefined)
            .map(
              _.focus(_.health).modify(health =>
                Math.min(
                  health + StructuresDefault.HOSPITAL_HEALTH_GAIN * h.treatmentQuality.qualityMultiplier,
                  e.maxHealth
                )
              )
            )
            .orElse(Some(e))
        }
      )
    }
  yield env.update(structures = structures -- hospitals ++ hospitalsUpdated)

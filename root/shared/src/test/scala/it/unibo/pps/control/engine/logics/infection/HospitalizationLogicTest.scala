package it.unibo.pps.control.engine.logics.infection

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import weaver.monixcompat.SimpleTaskSuite
import it.unibo.pps.control.engine.logics.entitystate.HospitalizationLogic.HospitalizeEntityLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MIN_VALUES
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.Hospital
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.TestUtils.*
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import monix.eval.Task

object HospitalizationLogicTest extends SimpleTaskSuite:
  val baseEnv: Environment = Samples.sampleEnv
  val hospitalizationLogic: UpdateLogic = HospitalizeEntityLogic()
  val fullHospital: Hospital = Hospital(Point2D(10, 2), 0.5, 0)
  val hospital: Hospital = Hospital(Point2D(10, 2), 0.5, 10)

  test("Without hospital infected external entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv)
    yield expect(
      updatedEnv.structures.select[Hospital].isEmpty &&
        countEntitiesAtRisk(updatedEnv.externalEntities) == countEntitiesAtRisk(baseEnv.externalEntities)
    )
  }

  test("Without hospital infected internal entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv)
    yield expect(
      updatedEnv.structures.select[Hospital].isEmpty &&
        countEntitiesAtRisk(updatedEnv.internalEntities) == countEntitiesAtRisk(baseEnv.internalEntities)
    )
  }

  test("With a full Hospital infected external entities at risk can't do anything") {
    for
      env <- Task(baseEnv.update(structures = baseEnv.structures + fullHospital))
      updatedEnv <- hospitalizationLogic(env)
    yield expect(countEntitiesAtRisk(updatedEnv.externalEntities) == countEntitiesAtRisk(env.externalEntities))
  }

  test("With a full Hospital infected internal entities at risk can't do anything") {
    for
      env <- Task(baseEnv.update(structures = baseEnv.structures + fullHospital))
      updatedEnv <- hospitalizationLogic(env)
    yield expect(
      countEntitiesAtRisk(
        updatedEnv.structures
          .filter(!_.isInstanceOf[Hospital])
          .flatMap(_.entities)
          .map(_.entity)
      ) == countEntitiesAtRisk(baseEnv.internalEntities)
    )
  }

  test("With a non-full Hospital infected external entities are hospitalized") {
    for updatedEnv <- hospitalizationLogic(baseEnv.update(structures = baseEnv.structures + hospital))
    yield expect(countEntitiesAtRisk(updatedEnv.externalEntities) == 0)
  }

  test("With a non-full Hospital infected internal entities are hospitalized") {
    for updatedEnv <- hospitalizationLogic(baseEnv.update(structures = baseEnv.structures + hospital))
    yield expect(
      countEntitiesAtRisk(
        updatedEnv.structures
          .filter(!_.isInstanceOf[Hospital])
          .flatMap(_.entities)
          .map(_.entity)
      ) == 0
    )
  }

  private def countEntitiesAtRisk(entities: Set[SimulationEntity]): Int =
    entities.count(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT)

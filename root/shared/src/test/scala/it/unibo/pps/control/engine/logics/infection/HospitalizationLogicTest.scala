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

object HospitalizationLogicTest extends SimpleTaskSuite:
  val baseEnv: Environment = Samples.sampleEnv
  val hospitalizationLogic: UpdateLogic = HospitalizeEntityLogic()
  val fullHospital: Hospital = Hospital(Point2D(10, 2), 0.5, 0)
  val hospital: Hospital = Hospital(Point2D(10, 2), 0.5, 10)

  test("Without hospital infected external entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv)
    yield expect(
      updatedEnv.structures.select[Hospital].isEmpty && updatedEnv.externalEntities.count(
        _.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT
      ) >= 0
    )
  }

  test("Without hospital infected internal entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv)
    yield expect(
      updatedEnv.structures.select[Hospital].isEmpty && updatedEnv.structures
        .flatMap(_.entities)
        .map(_.entity)
        .count(
          _.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT
        ) >= 0
    )
  }

  test("With a full Hospital infected external entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv.update(structures = baseEnv.structures + fullHospital))
    yield expect(updatedEnv.externalEntities.count(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT) >= 0)
  }

  test("With a full Hospital infected internal entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv.update(structures = baseEnv.structures + fullHospital))
    yield expect(
      updatedEnv.structures
        .filter(_.withCapabilities[Hospital].isEmpty)
        .flatMap(_.entities)
        .map(_.entity)
        .count(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT) >= 0
    )
  }

  test("With a non-full Hospital infected external entities are hospitalized") {
    for updatedEnv <- hospitalizationLogic(baseEnv.update(structures = baseEnv.structures + hospital))
    yield expect(updatedEnv.externalEntities.count(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT) == 0)
  }

  test("With a non-full Hospital infected internal entities are hospitalized") {
    for updatedEnv <- hospitalizationLogic(baseEnv.update(structures = baseEnv.structures + hospital))
    yield expect(
      updatedEnv.structures
        .filter(_.withCapabilities[Hospital].isEmpty)
        .flatMap(_.entities)
        .map(_.entity)
        .count(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT) == 0
    )
  }

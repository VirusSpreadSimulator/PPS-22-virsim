package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.MIN_VALUES
import it.unibo.pps.control.engine.logics.entitystate.HospitalizationLogic.HospitalizeEntityLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.TestUtils.*
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.{Hospital, SimulationStructure}
import monix.eval.Task
import weaver.monixcompat.SimpleTaskSuite

object HospitalizationLogicTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val hospitalizationLogic: UpdateLogic = HospitalizeEntityLogic()
  private val fullHospital: Hospital = Hospital(Point2D(10, 2), 0.5, 0)
  private val hospital: Hospital = Hospital(Point2D(10, 2), 0.5, 10)
  private val lowCapacityHospitals: Seq[Hospital] =
    Seq(Hospital(Point2D(10, 2), 0.5, 1), Hospital(Point2D(10, 3), 0.5, 1))

  test("Without hospital infected external entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv)
    yield expect(
      updatedEnv.structures.select[SimulationStructure with Hospitalization].isEmpty &&
        countEntitiesAtRisk(updatedEnv.externalEntities) == countEntitiesAtRisk(baseEnv.externalEntities)
    )
  }

  test("Without hospital infected internal entities at risk can't do anything") {
    for updatedEnv <- hospitalizationLogic(baseEnv)
    yield expect(
      updatedEnv.structures.select[SimulationStructure with Hospitalization].isEmpty &&
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
          .filter(!_.isInstanceOf[SimulationStructure with Hospitalization])
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
          .filter(!_.isInstanceOf[SimulationStructure with Hospitalization])
          .flatMap(_.entities)
          .map(_.entity)
      ) == 0
    )
  }

  test("All the hospitals capacity are filled then the remaining continue without being hospitalized") {
    for updatedEnv <- hospitalizationLogic(baseEnv.update(structures = baseEnv.structures ++ lowCapacityHospitals))
    yield expect(
      updatedEnv.structures
        .select[SimulationStructure with Hospitalization]
        .forall(s => s.entities.size == s.capacity) && countEntitiesAtRisk(
        updatedEnv.externalEntities
      ) + countEntitiesAtRisk(
        updatedEnv.structures.filter(!_.isInstanceOf[Hospitalization]).flatMap(_.entities.map(_.entity))
      ) > 0
    )
  }

  private def countEntitiesAtRisk(entities: Set[SimulationEntity]): Int =
    entities.count(_.health < MIN_VALUES.HOSPITALIZATION_HEALTH_LIMIT)

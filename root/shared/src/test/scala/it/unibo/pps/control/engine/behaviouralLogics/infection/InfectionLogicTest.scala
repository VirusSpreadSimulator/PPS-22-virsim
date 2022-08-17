package it.unibo.pps.control.engine.behaviouralLogics.infection

import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.control.engine.behaviouralLogics.infection.InfectionLogic.{
  ExternalInfectionLogic,
  InfectingEntity,
  InternalInfectionLogic
}
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.common.Utils.*
import weaver.monixcompat.SimpleTaskSuite

object InfectionLogicTest extends SimpleTaskSuite:
  val baseEnv: Environment = Samples.sampleEnv
  val externalInfectionLogic: ExternalInfectionLogic = ExternalInfectionLogic()
  val internalInfectionLogic: InternalInfectionLogic = InternalInfectionLogic()

  test("The external infection logic returns an updated env") {
    for updatedEnv <- externalInfectionLogic(baseEnv)
    yield expect(!(updatedEnv eq baseEnv))
  }

  test("External infection logic doesn't create spurious entities") {
    for updatedEnv <- externalInfectionLogic(baseEnv)
    yield expect(
      updatedEnv.externalEntities.size == baseEnv.externalEntities.size &&
        updatedEnv.structures.flatMap(_.entities).size == baseEnv.structures.flatMap(_.entities).size
    )
  }

  test("When the external infection logic is applied the number of external infected is more or equal") {
    for updatedEnv <- externalInfectionLogic(baseEnv)
    yield expect(numberOfInfected(updatedEnv.externalEntities) >= numberOfInfected(baseEnv.externalEntities))
  }

  test("In external infection logic the entities that are internal to structures are not considered") {
    for updatedEnv <- externalInfectionLogic(baseEnv)
    yield expect(numberOfInternalInfected(updatedEnv) == numberOfInternalInfected(baseEnv))
  }

  test("The internal infection logic returns an updated env") {
    for updatedEnv <- internalInfectionLogic(baseEnv)
    yield expect(!(updatedEnv eq baseEnv))
  }

  test("Internal infection logic doesn't create spurious entities") {
    for updatedEnv <- internalInfectionLogic(baseEnv)
    yield expect(
      updatedEnv.externalEntities.size == baseEnv.externalEntities.size &&
        updatedEnv.structures.flatMap(_.entities).size == baseEnv.structures.flatMap(_.entities).size
    )
  }

  test("Internal infection logic doesn't create spurious structures") {
    for updatedEnv <- internalInfectionLogic(baseEnv)
    yield expect(baseEnv.structures.size == updatedEnv.structures.size)
  }

  test("When the internal infection logic is applied the number of internal infected is more or equal") {
    for updatedEnv <- internalInfectionLogic(baseEnv)
    yield expect(numberOfInternalInfected(updatedEnv) >= numberOfInternalInfected(baseEnv))
  }

  test("In internal infection logic the entities that are external to structures are not considered") {
    for updatedEnv <- internalInfectionLogic(baseEnv)
    yield expect(numberOfInfected(baseEnv.externalEntities) == numberOfInfected(updatedEnv.externalEntities))
  }

  test("A structure without infected entities don't generate new infections") {
    for
      updatedEnv <- internalInfectionLogic(baseEnv)
      structuresWithoutInfectionBefore = baseEnv.structures
        .count(s => numberOfInfected(s.entities.map(_.entity)) == 0)
      structuresWithoutInfectionAfter = updatedEnv.structures.count(s =>
        numberOfInfected(s.entities.map(_.entity)) == 0
      )
    yield expect(structuresWithoutInfectionBefore == structuresWithoutInfectionAfter)
  }

  private def numberOfInfected[A](entities: Set[SimulationEntity]): Int =
    entities.select[InfectingEntity].count(_.infection.isDefined)

  private def numberOfInternalInfected(env: Environment): Int =
    numberOfInfected(
      env.structures
        .flatMap(_.entities)
        .map(_.entity)
    )

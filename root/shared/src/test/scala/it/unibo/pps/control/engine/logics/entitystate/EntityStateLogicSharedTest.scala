package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.UpdateEntityStateLogic
import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{MAX_VALUES, MIN_VALUES}
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.TestUtils.*
import weaver.monixcompat.SimpleTaskSuite

/** Shared test for entity state logic. */
object EntityStateLogicSharedTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val entityStateLogic: UpdateLogic = UpdateEntityStateLogic()

  test("The logic returns an updated env") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(!(updatedEnv eq baseEnv))
  }

  test("Entities health must be within their max health") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(updatedEnv.allEntities.forall(e => e.health <= e.maxHealth))
  }

  test("Alive entities must have health greater than min health") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(updatedEnv.allEntities.forall(_.health > MIN_VALUES.MIN_HEALTH))
  }

  test("Dead entities cardinality must be greater or equal than before") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(updatedEnv.deadEntities.size >= baseEnv.deadEntities.size)
  }

  test("Dead entities must have health equal to min") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(updatedEnv.deadEntities.forall(_.health == MIN_VALUES.MIN_HEALTH))
  }

  test("When an entity recover from virus then the immunity must increase") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      infectedBefore = baseEnv.allEntities.filter(_.infection.isDefined).map(_.id)
      entitiesToConsider = updatedEnv.allEntities.filter(e => e.infection.isEmpty && infectedBefore.contains(e.id))
    yield expect(
      baseEnv.allEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalImmunity < entitiesToConsider.totalImmunity
    )
  }

  test("Infected entities immunity remains constant during infection") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      entitiesToConsider = updatedEnv.allEntities.filter(_.infection.isDefined)
    yield expect(
      baseEnv.allEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalImmunity == entitiesToConsider.totalImmunity
    )
  }

  test("Entities immunity must be higher than min") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(updatedEnv.allEntities.forall(_.immunity >= MIN_VALUES.MIN_IMMUNITY))
  }

  test("Entities immunity must be within max") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(updatedEnv.allEntities.forall(_.immunity <= MAX_VALUES.MAX_IMMUNITY))
  }

  test("The sum of all alive and dead entities remain the same") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(
      baseEnv.allEntities.size + baseEnv.deadEntities.size == updatedEnv.allEntities.size + updatedEnv.deadEntities.size
    )
  }

  test("During the update no spurious structures are created") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(baseEnv.structures.size == updatedEnv.structures.size)
  }

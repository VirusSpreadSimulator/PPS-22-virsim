package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.UpdateEntityStateLogic
import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{MAX_VALUES, MIN_VALUES}
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.TestUtils.*
import weaver.monixcompat.SimpleTaskSuite

object EntityStateLogicTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val entityStateLogic: UpdateLogic = UpdateEntityStateLogic()

  test("The logic returns an updated env") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(!(updatedEnv eq baseEnv))
  }

  test("External healthy entities health must be increased") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      // Necessary because entities can recovery from the infection and then they would participate in the computation
      entitiesToConsider = baseEnv.externalEntities.filter(_.infection.isEmpty)
    yield expect(
      entitiesToConsider.totalHealth < updatedEnv.externalEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalHealth
    )
  }

  test("Internal to structure healthy entities health must be increased") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      entitiesToConsider = baseEnv.internalEntities.filter(_.infection.isEmpty)
    yield expect(
      entitiesToConsider.totalHealth < updatedEnv.internalEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalHealth
    )
  }

  test("External infected entities health must be decreased") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      entitiesToConsider = updatedEnv.externalEntities.filter(_.infection.isDefined)
    yield expect(
      baseEnv.externalEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalHealth > entitiesToConsider.totalHealth
    )
  }

  test("Internal to structure infected entities health must be decreased") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      entitiesToConsider = updatedEnv.internalEntities.filter(_.infection.isDefined)
    yield expect(
      baseEnv.internalEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalHealth > entitiesToConsider.totalHealth
    )
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

  test("Dead entities must have health equal or less to min") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(updatedEnv.deadEntities.forall(_.health <= MIN_VALUES.MIN_HEALTH))
  }

  test("External healthy entities immunity must be decreased") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      entitiesToConsider = baseEnv.externalEntities.filter(_.infection.isEmpty)
    yield expect(
      entitiesToConsider.totalImmunity > updatedEnv.externalEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalImmunity
    )
  }

  test("Internal to structure healthy entities immunity must be decreased") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      entitiesToConsider = baseEnv.internalEntities.filter(_.infection.isEmpty)
    yield expect(
      entitiesToConsider.totalImmunity > updatedEnv.internalEntities
        .filter(e => entitiesToConsider.map(_.id).contains(e.id))
        .totalImmunity
    )
  }

  test("When an entity recover from virus then the immunity must increase") {
    for
      updatedEnv <- entityStateLogic(baseEnv)
      entitiesToConsider = updatedEnv.allEntities.filter(e =>
        e.infection.isEmpty && baseEnv.allEntities.filter(_.infection.isDefined).map(_.id).contains(e.id)
      )
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

  test("It must not exist an infection that is over in external entities") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(
      updatedEnv.externalEntities
        .filter(_.infection.isDefined)
        .map(_.infection.get)
        .forall(i => i.timeOfTheInfection + i.duration > updatedEnv.time)
    )
  }

  test("It must not exist an infection that is over in internal to structures entities") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(
      updatedEnv.internalEntities
        .filter(_.infection.isDefined)
        .map(_.infection.get)
        .forall(i => i.timeOfTheInfection + i.duration > updatedEnv.time)
    )
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

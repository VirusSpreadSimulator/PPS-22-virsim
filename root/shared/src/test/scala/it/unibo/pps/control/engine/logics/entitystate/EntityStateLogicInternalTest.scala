package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.UpdateEntityStateLogic
import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.TestUtils.*
import weaver.monixcompat.SimpleTaskSuite

/** Test for entity state logic. Here we test internal to structures */
object EntityStateLogicInternalTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val entityStateLogic: UpdateLogic = UpdateEntityStateLogic()

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

  test("It must not exist an infection that is over in internal to structures entities") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(
      updatedEnv.internalEntities
        .filter(_.infection.isDefined)
        .map(_.infection.get)
        .forall(i => i.timeOfTheInfection + i.duration > updatedEnv.time)
    )
  }

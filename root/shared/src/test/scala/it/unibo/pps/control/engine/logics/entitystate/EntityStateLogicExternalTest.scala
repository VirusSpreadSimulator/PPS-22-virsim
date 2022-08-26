package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.entitystate.EntityStateLogic.UpdateEntityStateLogic
import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import weaver.monixcompat.SimpleTaskSuite
import it.unibo.pps.entity.TestUtils.*

/** Test for entity state logic. Here we test external to structures */
object EntityStateLogicExternalTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val entityStateLogic: UpdateLogic = UpdateEntityStateLogic()

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

  test("It must not exist an infection that is over in external entities") {
    for updatedEnv <- entityStateLogic(baseEnv)
    yield expect(
      updatedEnv.externalEntities
        .filter(_.infection.isDefined)
        .map(_.infection.get)
        .forall(i => i.timeOfTheInfection + i.duration > updatedEnv.time)
    )
  }

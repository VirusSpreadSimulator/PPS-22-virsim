package it.unibo.pps.control.engine.logics.entitystate

import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.TestUtils.*
import weaver.monixcompat.SimpleTaskSuite

object HospitalRecoveryLogicTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val env: Environment = baseEnv.update(structures = baseEnv.structures + Samples.inhabitatedHospital)
  private val hospitalRecoveryLogic: UpdateLogic = HospitalRecoveryLogic()

  test("Health of infected entity inside hospitals increase") {
    for updatedEnv <- hospitalRecoveryLogic(env)
    yield expect(
      updatedEnv.hospitals
        .flatMap(_.entities.map(_.entity))
        .filter(_.infection.isDefined)
        .totalHealth > env.hospitals
        .flatMap(_.entities.map(_.entity))
        .filter(_.infection.isDefined)
        .totalHealth
    )
  }

  test("Health of not infected entity inside hospitals remain the same") {
    for updatedEnv <- hospitalRecoveryLogic(env)
    yield expect(
      updatedEnv.hospitals
        .flatMap(_.entities.map(_.entity))
        .filter(_.infection.isEmpty)
        .totalHealth == env.hospitals
        .flatMap(_.entities.map(_.entity))
        .filter(_.infection.isEmpty)
        .totalHealth
    )
  }

  test("Health of external entities remain the same") {
    for updatedEnv <- hospitalRecoveryLogic(env)
    yield expect(env.externalEntities.totalHealth == updatedEnv.externalEntities.totalHealth)
  }

  test("The logic doesn't generate new structures") {
    for updatedEnv <- hospitalRecoveryLogic(env)
    yield expect(env.structures.size == updatedEnv.structures.size)
  }

  test("The logic don't generate or delete entities") {
    for updatedEnv <- hospitalRecoveryLogic(env)
    yield expect(env.allEntities.size == updatedEnv.allEntities.size)
  }

  test("Entities don't get more health than allowed") {
    for updatedEnv <- hospitalRecoveryLogic(env)
    yield expect(updatedEnv.allEntities.forall(e => e.health <= e.maxHealth))
  }

package it.unibo.pps.control.engine.logics

import it.unibo.pps.control.engine.logics.Logic.EventLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task
import weaver.monixcompat.SimpleTaskSuite

object VaccinationLogicTest extends SimpleTaskSuite:

  private val env: Environment = Samples.sampleEnv
  private val vaccinationPercentage: Double = 50
  private val vaccinationLogic: EventLogic = EventLogic.vaccineRound(vaccinationPercentage)

  test("The logic returns an updated env") {
    for updatedEnv <- vaccinationLogic(env)
    yield expect(!(updatedEnv eq env))
  }

  test("The logic should not increase the number of entities") {
    for updatedEnv <- vaccinationLogic(env)
    yield expect(env.externalEntities.size == updatedEnv.externalEntities.size)
  }

  test("The logic should vaccinate the correct percentage") {
    for
      updatedEnv <- vaccinationLogic(env)
      oldEntities = env.externalEntities
      updatedEntities = updatedEnv.externalEntities
    yield expect(updatedEntities.diff(oldEntities).size == env.externalEntities.size * vaccinationPercentage / 100)
  }

  test("The logic should correctly increase the immunity of entities") {
    val totalVaccinationLogic: EventLogic = EventLogic.vaccineRound(100)
    for
      updatedEnv <- totalVaccinationLogic(env)
      newImmunities = updatedEnv.externalEntities
        .map(_.immunity)
        .toIndexedSeq
    yield expect(
      !newImmunities.indices
        .exists(i => newImmunities(i) < env.externalEntities.map(_.immunity).toIndexedSeq(i))
    )
  }

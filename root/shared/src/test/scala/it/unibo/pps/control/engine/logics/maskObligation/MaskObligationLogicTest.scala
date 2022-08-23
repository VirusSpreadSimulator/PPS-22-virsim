package it.unibo.pps.control.engine.logics.maskObligation

import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import weaver.monixcompat.SimpleTaskSuite
import it.unibo.pps.control.engine.logics.maskObligation.MaskObligationLogic.SwitchMaskObligationLogic

object MaskObligationLogicTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val switchMaskObligationLogic = SwitchMaskObligationLogic()

  test("after add mask logic all entities have a mask") {
    for withMaskEnv <- switchMaskObligationLogic(baseEnv)
    yield expect(withMaskEnv.allEntities.forall(_.hasMask))
  }

  test("when the entities have mask, with remove mask logic, no entities will have a mask") {
    for
      withMaskEnv <- switchMaskObligationLogic(baseEnv)
      withoutMaskEnv <- switchMaskObligationLogic(withMaskEnv)
    yield expect(withoutMaskEnv.allEntities.forall(!_.hasMask))
  }

  test("add mask logic doesn't modify the number of entities") {
    for withMaskEnv <- switchMaskObligationLogic(baseEnv)
    yield expect(withMaskEnv.allEntities.size == baseEnv.allEntities.size)
  }

  test("remove mask logic doesn't modify the number of entities") {
    for
      withMaskEnv <- switchMaskObligationLogic(baseEnv)
      withoutMaskEnv <- switchMaskObligationLogic(withMaskEnv)
    yield expect(withoutMaskEnv.allEntities.size == baseEnv.allEntities.size)
  }

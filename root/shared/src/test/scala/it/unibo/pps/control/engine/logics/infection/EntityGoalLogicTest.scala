package it.unibo.pps.control.engine.logics.infection

import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import weaver.monixcompat.SimpleTaskSuite
import it.unibo.pps.control.engine.logics.Logic.UpdateLogic
import it.unibo.pps.control.engine.logics.entitygoal.EntityGoalLogic.EntityGoalUpdateLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.common.Time.{TimeConfiguration, TimeStamp}

object EntityGoalLogicTest extends SimpleTaskSuite:
  private val baseEnv: Environment = Samples.sampleEnv
  private val entityGoalLogic: UpdateLogic = EntityGoalUpdateLogic()
  private val day: Int = 2
  private val dayTimeStamp: TimeStamp =
    TimeStamp(relativeTicks = TimeConfiguration.DAY_MINUTES_UPPER_BOUND - 10, iteration = day)
  private val startNightTimeStamp: TimeStamp =
    TimeStamp(relativeTicks = TimeConfiguration.DAY_MINUTES_UPPER_BOUND, iteration = day)

  test("At the start of the day all the external entities need to move randomly") {
    for updatedEnv <- entityGoalLogic(baseEnv.update(time = TimeStamp(iteration = day)))
    yield expect(updatedEnv.externalEntities.forall(_.movementGoal == MovementGoal.RANDOM_MOVEMENT))
  }

  test("During the day all the external entities need to move randomly") {
    for updatedEnv <- entityGoalLogic(baseEnv.update(time = dayTimeStamp))
    yield expect(updatedEnv.externalEntities.forall(_.movementGoal == MovementGoal.RANDOM_MOVEMENT))
  }

  test("At the start of the night some external entities may want to return to home") {
    for updatedEnv <- entityGoalLogic(baseEnv.update(time = startNightTimeStamp))
    yield expect(updatedEnv.externalEntities.count(_.movementGoal == MovementGoal.BACK_TO_HOME) >= 0)
  }

  test("In changing the goal no new entities are created") {
    for updatedEnv <- entityGoalLogic(baseEnv.update(time = startNightTimeStamp))
    yield expect(baseEnv.externalEntities.size == updatedEnv.externalEntities.size)
  }

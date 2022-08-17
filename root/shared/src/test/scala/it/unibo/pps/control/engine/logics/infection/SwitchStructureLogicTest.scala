package it.unibo.pps.control.engine.logics.infection

import it.unibo.pps.control.engine.logics.Logic.EventLogic
import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import weaver.monixcompat.SimpleTaskSuite
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Groupable}
import it.unibo.pps.entity.structure.Structures.SimulationStructure

/* Here, for simplicity, we consider a group of structures that have all the same starting opening state */
object SwitchStructureLogicTest extends SimpleTaskSuite:
  val env: Environment = Samples.sampleEnv
  val analyzedGroup: String = "group1"
  val switchLogic: EventLogic = EventLogic.switchStructure(analyzedGroup)

  test("When switch is performed all the structure of the same group switch") {
    for updatedEnv <- switchLogic(env)
    yield expect(getGroupStatus(env, analyzedGroup) != getGroupStatus(updatedEnv, analyzedGroup))
  }

  test("When switch is performed structures with different group id are not considered") {
    for
      updatedEnv <- switchLogic(env)
      previousStatuses = getStatuses(env).filter((k, _) => k != analyzedGroup)
      updatedStatuses = getStatuses(updatedEnv).filter((k, _) => k != analyzedGroup)
    yield expect(previousStatuses == updatedStatuses)
  }

  test("Switch works as a switch, so after two switch it's in the starting status") {
    for
      updatedEnv <- switchLogic(env)
      finalEnv <- switchLogic(updatedEnv)
    yield expect(getGroupStatus(env, analyzedGroup) == getGroupStatus(finalEnv, analyzedGroup))
  }

  private def getStatuses(e: Environment): Map[Any, Boolean] = e.structures
    .select[SimulationStructure with Closable with Groupable]
    .groupBy(_.group)
    .map((group, v) => (group, v.map(_.isOpen).reduce(_ && _)))

  private def getGroupStatus(e: Environment, group: String): Boolean =
    e.structures.select[Closable with Groupable].filter(_.group == group).map(_.isOpen).reduce(_ && _)

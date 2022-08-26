package it.unibo.pps.control.engine.logics.entrance

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, House, SimulationStructure}
import weaver.monixcompat.SimpleTaskSuite

object EntranceLogicTest extends SimpleTaskSuite:

  private val house = House((1, 0), 1, 2)
  private val enterIntoStructureLogic: EntranceLogic = EntranceLogic()
  private val entitiesNear: Seq[SimulationEntity] = Seq(
    SimulationEntity(0, 20, house.position, 80, position = Point2D(0, 20))
  )

  private val entitiesNotNear: Seq[SimulationEntity] = Seq(
    SimulationEntity(0, 20, house.position, 80, position = Point2D(10, 10))
  )

  private val buildings: Set[SimulationStructure] = Set(
    GenericBuilding(
      Point2D(0, 21),
      0.5,
      4
    )
  )

  def firstEnv: Environment =
    object InfectedEnv extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(externalEntities = entitiesNear.toSet, structures = buildings)
    InfectedEnv.env

  def secondEnv: Environment =
    object InfectedEnv extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(externalEntities = entitiesNotNear.toSet, structures = buildings)
    InfectedEnv.env

  test("an entity near to a building can enter") {
    for updatedEnv <- enterIntoStructureLogic(firstEnv)
    yield expect(numberOfExternalEntities(updatedEnv) != numberOfExternalEntities(firstEnv))
  }

  test("en entity not near to a building can't enter") {
    for updatedEnv <- enterIntoStructureLogic(secondEnv)
    yield expect(numberOfExternalEntities(updatedEnv) == numberOfExternalEntities(secondEnv))
  }

  test("when an entity enter, the number of the entities in the environment remains the same") {
    for updatedEnv <- enterIntoStructureLogic(firstEnv)
    yield expect(numberOfAllEntities(updatedEnv) == numberOfAllEntities(firstEnv))
  }

  test("when an entity doesn't enter, the number of the entities in the environment remains the same") {
    for updatedEnv <- enterIntoStructureLogic(firstEnv)
    yield expect(numberOfAllEntities(updatedEnv) == numberOfAllEntities(firstEnv))
  }

  private def numberOfExternalEntities(env: Environment): Int =
    env.externalEntities.size

  private def numberOfAllEntities(env: Environment): Int =
    env.allEntities.size

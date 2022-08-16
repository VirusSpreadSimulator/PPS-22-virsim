package it.unibo.pps.control.engine.behaviouralLogics.infection

import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.entity.Infection
import it.unibo.pps.entity.entity.Infection.Severity
import it.unibo.pps.entity.structure.Structures.House
import weaver.monixcompat.SimpleTaskSuite
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.control.engine.behaviouralLogics.infection.InfectionLogic.{ExternalInfectionLogic, InfectingEntity}

object InfectionLogicTest extends SimpleTaskSuite:
  private val house = House((1, 0), 1, 2)
  val entities: Set[SimulationEntity] = Set(
    BaseEntity(0, 20, house, position = Point2D(1, 2)),
    BaseEntity(1, 21, house, position = Point2D(3, 2)),
    BaseEntity(2, 22, house, position = Point2D(4, 5)),
    BaseEntity(0, 20, house, position = Point2D(4, 2), infection = Some(Infection(Severity.LIGHT(), 5))),
    BaseEntity(0, 20, house, position = Point2D(1, 1), infection = Some(Infection(Severity.LIGHT(), 5)))
  )

  object InfectedEnv extends EnvironmentModule.Interface:
    val env = EnvironmentImpl(entities = entities)

  val externalInfectionLogic: ExternalInfectionLogic = ExternalInfectionLogic()

  test("The logic returns an updated env") {
    for {
      updatedEnv <- externalInfectionLogic(InfectedEnv.env)
    } yield expect(!(updatedEnv eq InfectedEnv.env))
  }

  test("External infection logic doesn't create spurious entities") {
    for {
      updatedEnv <- externalInfectionLogic(InfectedEnv.env)
    } yield expect(updatedEnv.entities.size == InfectedEnv.env.entities.size)
  }

  test("When the external infection logic is applied the number of infected is more or equal") {
    for {
      updatedEnv <- externalInfectionLogic(InfectedEnv.env)
    } yield expect(numberOfInfected(updatedEnv.entities) >= numberOfInfected(InfectedEnv.env.entities))
  }

  private def numberOfInfected(entities: Set[SimulationEntity]): Int =
    entities.select[InfectingEntity].count(_.infection.isDefined)

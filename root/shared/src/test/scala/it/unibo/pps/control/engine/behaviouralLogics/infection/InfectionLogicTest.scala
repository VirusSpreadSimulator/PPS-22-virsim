package it.unibo.pps.control.engine.behaviouralLogics.infection

import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.entity.Infection
import it.unibo.pps.entity.entity.Infection.Severity
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, House, SimulationStructure}
import weaver.monixcompat.SimpleTaskSuite
import it.unibo.pps.entity.common.Utils.*
import it.unibo.pps.control.engine.behaviouralLogics.infection.InfectionLogic.{
  ExternalInfectionLogic,
  InfectingEntity,
  InternalInfectionLogic
}
import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence

import scala.concurrent.duration.MINUTES

object InfectionLogicTest extends SimpleTaskSuite:
  private val house = House((1, 0), 1, 2)
  val entities: Seq[SimulationEntity] = Seq(
    BaseEntity(0, 20, house, position = Point2D(1, 2)),
    BaseEntity(1, 21, house, position = Point2D(3, 2)),
    BaseEntity(2, 22, house, position = Point2D(4, 5)),
    BaseEntity(3, 20, house, position = Point2D(4, 2), infection = Some(Infection(Severity.LIGHT(), 5))),
    BaseEntity(4, 20, house, position = Point2D(1, 1), infection = Some(Infection(Severity.LIGHT(), 5)))
  )

  val buildings: Set[SimulationStructure] = Set(
    GenericBuilding(
      Point2D(1, 7),
      0.5,
      4,
      entities = Set(
        EntityPermanence(entities(0), TimeStamp(), DurationTime(11, MINUTES)),
        EntityPermanence(entities(3), TimeStamp(), DurationTime(11, MINUTES)),
        EntityPermanence(entities(4), TimeStamp(), DurationTime(11, MINUTES))
      )
    ),
    GenericBuilding(
      Point2D(8, 7),
      0.5,
      4,
      entities = Set(
        EntityPermanence(entities(1), TimeStamp(), DurationTime(11, MINUTES)),
        EntityPermanence(entities(2), TimeStamp(), DurationTime(11, MINUTES))
      )
    )
  )

  object InfectedEnv extends EnvironmentModule.Interface:
    val env = EnvironmentImpl(externalEntities = entities.toSet, structures = buildings)

  val externalInfectionLogic: ExternalInfectionLogic = ExternalInfectionLogic()
  val internalInfectionLogic: InternalInfectionLogic = InternalInfectionLogic()

  //todo: riconsidera ogni test con la versione modificata di environment
  test("The external infection logic returns an updated env") {
    for {
      updatedEnv <- externalInfectionLogic(InfectedEnv.env)
    } yield expect(!(updatedEnv eq InfectedEnv.env))
  }

  test("External infection logic doesn't create spurious entities") {
    for {
      updatedEnv <- externalInfectionLogic(InfectedEnv.env)
    } yield expect(
      updatedEnv.externalEntities.size == InfectedEnv.env.externalEntities.size &&
        updatedEnv.structures.map(_.entities).size == InfectedEnv.env.structures.map(_.entities).size
    )
  }

  test("When the external infection logic is applied the number of infected is more or equal") {
    for {
      updatedEnv <- externalInfectionLogic(InfectedEnv.env)
    } yield expect(numberOfInfected(updatedEnv.externalEntities) >= numberOfInfected(InfectedEnv.env.externalEntities))
  }

  test("In external infection logic the entities that are internal to structures are not considered") {
    for {
      updatedEnv <- externalInfectionLogic(InfectedEnv.env)
    } yield expect(numberOfInternalInfected(updatedEnv) == numberOfInternalInfected(InfectedEnv.env))
  }

  test("The internal infection logic returns an updated env") {
    for {
      updatedEnv <- internalInfectionLogic(InfectedEnv.env)
    } yield expect(!(updatedEnv eq InfectedEnv.env))
  }

  test("Internal infection logic doesn't create spurious entities") {
    for {
      updatedEnv <- internalInfectionLogic(InfectedEnv.env)
    } yield expect(
      updatedEnv.externalEntities.size == InfectedEnv.env.externalEntities.size &&
        updatedEnv.structures.map(_.entities).size == InfectedEnv.env.structures.map(_.entities).size
    )
  }

  test("When the internal infection logic is applied the number of internal infected is more or equal") {
    for {
      updatedEnv <- internalInfectionLogic(InfectedEnv.env)
      _ <- monix.eval.Task(println(numberOfInfected(updatedEnv.externalEntities)))
      _ <- monix.eval.Task(println(numberOfInternalInfected(updatedEnv)))
    } yield expect(numberOfInternalInfected(updatedEnv) >= numberOfInternalInfected(InfectedEnv.env))
  }

  // todo: test che quando la si applica le entità esterne alle strutture non vengono toccate

  test("A structure without infected entities don't generate new infections") {
    for {
      updatedEnv <- internalInfectionLogic(InfectedEnv.env)
      structuresWithoutInfectionBefore = InfectedEnv.env.structures
        .count(s => numberOfInfected(s.entities.map(_.entity).select[SimulationEntity]) == 0)
      structuresWithoutInfectionAfter = updatedEnv.structures.count(s =>
        numberOfInfected(s.entities.map(_.entity).select[SimulationEntity]) == 0
      )
    } yield expect(structuresWithoutInfectionBefore == structuresWithoutInfectionAfter)
  }

  private def numberOfInfected[A](entities: Set[SimulationEntity]): Int =
    entities.select[InfectingEntity].count(_.infection.isDefined)

  private def numberOfInternalInfected(env: Environment): Int =
    numberOfInfected(
      env.structures
        .flatMap(_.entities)
        .map(_.entity)
        .select[SimulationEntity]
    )

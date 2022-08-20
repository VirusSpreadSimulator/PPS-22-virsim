package it.unibo.pps.entity

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Infection
import it.unibo.pps.entity.entity.Infection.Severity
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence

import scala.concurrent.duration.{DAYS, MINUTES}

object Samples:
  private val house = House((1, 0), 1, 2)
  val genericInfectedPermanences: Set[EntityPermanence] = Set(
    EntityPermanence(
      SimulationEntity(1, 20, house.position, 80, position = Point2D(1, 7)),
      TimeStamp(),
      DurationTime(11, MINUTES)
    ),
    EntityPermanence(
      SimulationEntity(
        2,
        10,
        house.position,
        0.001,
        position = Point2D(1, 7),
        infection = Some(Infection(Severity.LIGHT(), TimeStamp(), DurationTime(5, DAYS)))
      ),
      TimeStamp(),
      DurationTime(11, MINUTES)
    ),
    EntityPermanence(
      SimulationEntity(
        3,
        20,
        house.position,
        80,
        position = Point2D(1, 7),
        infection = Some(Infection(Severity.LIGHT(), TimeStamp(), DurationTime(5, DAYS)))
      ),
      TimeStamp(),
      DurationTime(11, MINUTES)
    ),
    EntityPermanence(
      SimulationEntity(
        4,
        20,
        house.position,
        80,
        position = Point2D(1, 7),
        infection = Some(Infection(Severity.LIGHT(), TimeStamp(), DurationTime(0, MINUTES)))
      ),
      TimeStamp(),
      DurationTime(11, MINUTES)
    )
  )

  val inhabitatedHouse: House = House((1, 0), 1, 2, entities = genericInfectedPermanences)
  val inhabitatedGenericBuilding: GenericBuilding =
    GenericBuilding(Point2D(8, 7), 0.5, 4, entities = genericInfectedPermanences)
  val inhabitatedHospital: Hospital = Hospital((1, 0), 1, 2, entities = genericInfectedPermanences)

  private val entities: Set[SimulationEntity] = Set(
    SimulationEntity(5, 20, house.position, 80, 10, position = Point2D(1, 2)),
    SimulationEntity(6, 21, house.position, 80, 10, position = Point2D(3, 2)),
    SimulationEntity(7, 22, house.position, 80, 10, position = Point2D(4, 5)),
    SimulationEntity(
      8,
      10,
      house.position,
      0.001,
      70,
      position = Point2D(4, 2),
      infection = Some(Infection(Severity.LIGHT(), TimeStamp(), DurationTime(0, MINUTES)))
    ),
    SimulationEntity(
      9,
      20,
      house.position,
      80,
      20,
      position = Point2D(1, 1),
      infection = Some(Infection(Severity.LIGHT(), TimeStamp(), DurationTime(5, DAYS)))
    ),
    SimulationEntity(
      10,
      20,
      house.position,
      80,
      10,
      position = Point2D(1, 1),
      infection = Some(Infection(Severity.LIGHT(), TimeStamp(), DurationTime(0, MINUTES)))
    )
  )

  private val buildings: Set[SimulationStructure] = Set(
    GenericBuilding(Point2D(1, 7), 0.5, 4, entities = genericInfectedPermanences),
    GenericBuilding(
      Point2D(8, 7),
      0.5,
      4,
      entities = Set(
        EntityPermanence(
          SimulationEntity(11, 21, house.position, 80, 10, position = Point2D(8, 7)),
          TimeStamp(),
          DurationTime(0, MINUTES)
        ),
        EntityPermanence(
          SimulationEntity(12, 21, house.position, 80, 20, position = Point2D(8, 7)),
          TimeStamp(),
          DurationTime(11, MINUTES)
        )
      )
    ),
    GenericBuilding(
      Point2D(4, 5),
      0.3,
      4,
      group = "group1"
    ),
    GenericBuilding(
      Point2D(1, 5),
      0.3,
      4,
      group = "group1"
    )
  )

  def sampleEnv: Environment =
    object InfectedEnv extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(externalEntities = entities, structures = buildings)
    InfectedEnv.env

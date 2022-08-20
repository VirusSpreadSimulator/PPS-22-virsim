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
  private def permanence(
      id: Int,
      age: Int,
      housePosition: Point2D,
      health: Double,
      position: Point2D,
      infectionDuration: Option[DurationTime] = None,
      immunity: Double = 0,
      duration: DurationTime = DurationTime(11, MINUTES)
  ) =
    EntityPermanence(
      SimulationEntity(
        id,
        age,
        housePosition,
        health,
        immunity,
        position = position,
        infection =
          if infectionDuration.isDefined then Some(Infection(Severity.LIGHT(), TimeStamp(), infectionDuration.get))
          else None
      ),
      TimeStamp(),
      duration
    )

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
    GenericBuilding(
      Point2D(1, 7),
      0.5,
      4,
      entities = Set(
        permanence(11, 20, house.position, 80, Point2D(1, 7)),
        permanence(12, 10, house.position, 0.001, Point2D(1, 7), Some(DurationTime(5, DAYS))),
        permanence(13, 20, house.position, 80, Point2D(1, 7), Some(DurationTime(5, DAYS))),
        permanence(14, 20, house.position, 80, Point2D(1, 7), Some(DurationTime(0, MINUTES)))
      )
    ),
    GenericBuilding(
      Point2D(8, 7),
      0.5,
      4,
      entities = Set(
        permanence(15, 21, house.position, 80, Point2D(8, 7), None, 10, DurationTime(0, MINUTES)),
        permanence(16, 21, house.position, 80, Point2D(8, 7), None, 20, DurationTime(11, MINUTES))
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

  val genericInfectedPermanences: Set[EntityPermanence] = Set(
    permanence(1, 20, house.position, 80, Point2D(1, 7)),
    permanence(2, 10, house.position, 0.001, Point2D(1, 7), Some(DurationTime(5, DAYS))),
    permanence(3, 20, house.position, 80, Point2D(1, 7), Some(DurationTime(5, DAYS))),
    permanence(4, 20, house.position, 80, Point2D(1, 7), Some(DurationTime(0, MINUTES)))
  )
  val inhabitatedHouse: House = House((1, 0), 1, 2, entities = genericInfectedPermanences)
  val inhabitatedGenericBuilding: GenericBuilding =
    GenericBuilding(Point2D(8, 7), 0.5, 4, entities = genericInfectedPermanences)
  val inhabitatedHospital: Hospital = Hospital((1, 0), 1, 2, entities = genericInfectedPermanences)

  def sampleEnv: Environment =
    object InfectedEnv extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(externalEntities = entities, structures = buildings)
    InfectedEnv.env

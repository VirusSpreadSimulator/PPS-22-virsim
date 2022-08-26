package it.unibo.pps.entity

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Infection
import it.unibo.pps.entity.entity.Infection.Severity
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence
import scala.concurrent.duration.{DAYS, MINUTES}

/** Module that contains some simple samples useful during testing. */
object Samples:
  private val age = 20
  private val immunity = 70
  private val health = 80
  private val atRiskHealth = 0.001
  private val infectionProbability = 0.5
  private val capacity = 4
  private val durationLong = DurationTime(11, MINUTES)
  private val durationShort = DurationTime(0, MINUTES)
  private val house = House(Point2D(1, 0), infectionProbability, capacity)
  /** Method to create a permanence for an entity created on the fly.
    * @param id
    *   the id of the entity
    * @param age
    *   the age of the entity
    * @param housePosition
    *   the house position of the entity
    * @param health
    *   the health of the entity
    * @param position
    *   the position of the entity
    * @param infectionDuration
    *   the infection duration, an [[Option]] if [[None]] then there is no infection
    * @param immunity
    *   the immunity of the entity
    * @param duration
    *   the duration of the permanence
    * @return
    *   the [[EntityPermanence]]
    */
  private def permanence(
      id: Int,
      age: Int,
      housePosition: Point2D,
      health: Double,
      position: Point2D,
      infectionDuration: Option[DurationTime] = None,
      immunity: Double = 0,
      duration: DurationTime = durationLong
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
    SimulationEntity(5, age, house.position, health, immunity, position = Point2D(1, 2)),
    SimulationEntity(6, age, house.position, health, immunity, position = Point2D(3, 2)),
    SimulationEntity(7, age, house.position, health, immunity, position = Point2D(4, 5)),
    SimulationEntity(
      8,
      age,
      house.position,
      atRiskHealth,
      immunity,
      position = Point2D(4, 2),
      infection = Some(Infection(Severity.LIGHT(), TimeStamp(), durationLong))
    ),
    SimulationEntity(
      9,
      age,
      house.position,
      health,
      immunity,
      position = Point2D(1, 1),
      infection = Some(Infection(Severity.LIGHT(), TimeStamp(), durationLong))
    ),
    SimulationEntity(
      10,
      age,
      house.position,
      health,
      immunity,
      position = Point2D(1, 1),
      infection = Some(Infection(Severity.LIGHT(), TimeStamp(), durationShort))
    )
  )
  private val buildings: Set[SimulationStructure] = Set(
    GenericBuilding(
      Point2D(1, 7),
      infectionProbability,
      capacity,
      entities = Set(
        permanence(11, age, house.position, health, Point2D(1, 7)),
        permanence(12, age, house.position, atRiskHealth, Point2D(1, 7), Some(durationLong)),
        permanence(13, age, house.position, health, Point2D(1, 7), Some(durationLong)),
        permanence(14, age, house.position, atRiskHealth, Point2D(1, 7), Some(durationShort))
      )
    ),
    GenericBuilding(
      Point2D(8, 7),
      infectionProbability,
      capacity,
      entities = Set(
        permanence(15, age, house.position, health, Point2D(8, 7), None, 10, durationShort),
        permanence(16, age, house.position, health, Point2D(8, 7), None, 20, durationLong)
      )
    ),
    GenericBuilding(Point2D(4, 5), infectionProbability, capacity, group = "group1"),
    GenericBuilding(Point2D(1, 5), infectionProbability, capacity, group = "group1")
  )

  /** A set of [[EntityPermanence]] with some infected entities. */
  val genericInfectedPermanences: Set[EntityPermanence] = Set(
    permanence(1, age, house.position, health, Point2D(1, 7)),
    permanence(2, age, house.position, atRiskHealth, Point2D(1, 7), Some(durationLong)),
    permanence(3, age, house.position, health, Point2D(1, 7), Some(durationLong)),
    permanence(4, age, house.position, health, Point2D(1, 7), Some(durationShort))
  )
  /** An [[House]] with some [[EntityPermanence]]. Some of them are infected. */
  val inhabitatedHouse: House =
    House(Point2D(1, 0), infectionProbability, capacity, entities = genericInfectedPermanences)
  /** A [[GenericBuilding]] with some [[EntityPermanence]]. Some of them are infected. */
  val inhabitatedGenericBuilding: GenericBuilding =
    GenericBuilding(Point2D(8, 7), infectionProbability, capacity, entities = genericInfectedPermanences)
  /** An [[Hospital]] with some [[EntityPermanence]]. Some of them are infected. */
  val inhabitatedHospital: Hospital =
    Hospital((1, 0), infectionProbability, capacity, entities = genericInfectedPermanences)

  /** Method to create a sample env, with some infected entity inside.
    * @return
    *   the environment
    */
  def sampleEnv: Environment =
    object InfectedEnv extends EnvironmentModule.Interface:
      val env: Environment = EnvironmentImpl(externalEntities = entities, structures = buildings)
    InfectedEnv.env

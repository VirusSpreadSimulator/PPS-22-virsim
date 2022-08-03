package it.unibo.pps.entity.structure

import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import it.unibo.pps.entity.common.Space.{Distance, Point2D}
import it.unibo.pps.entity.structure.StructureComponent.*
import it.unibo.pps.entity.structure.entrance.Entrance.EntranceStrategy
import it.unibo.pps.entity.structure.entrance.Entrance.BaseEntranceStrategy
import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization.TreatmentQuality
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence

import scala.concurrent.duration.MINUTES
import monocle.syntax.all.*

/** This module contains the base structure description and the main implementation that are useful during the
  * simulation configuration
  */
object Structures:
  private val defaultVisibilityDistance = 2
  private val defaultGroup = "base"
  private val defaultPermanenceTimeDistribution = GaussianDurationTime(20, 5, MINUTES)

  /** It's the base [[Structure]] */
  trait BaseStructure extends Structure:
    override type Position = Point2D
    override type Probability = Double
    override type TimeDistribution = GaussianDurationTime

    override protected def checkEnter(entity: Entity): Boolean =
      entities.size < capacity && entranceStrategy.canEnter(entity)

  /** Builder for the House type of structure
    * @param infectionProbability
    *   the probability of infection inside the structure
    * @param capacity
    *   the capacity of the structure in terms of the number of entities that can enter
    * @param permanenceTimeDistribution
    *   gaussian distribution that describe the permanence time
    * @param entranceStrategy
    *   the strategy used for discriminate the entities entrance
    * @param entities
    *   the entities that are inside the structure.
    */
  case class House(
      override val position: Point2D,
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime = defaultPermanenceTimeDistribution,
      override val entities: Set[EntityPermanence] = Set()
  ) extends BaseStructure
      with Habitable:
    override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy()
    override protected def enter(entity: String, timestamp: TimeStamp): Structure =
      this.focus(_.entities).modify(_ + EntityPermanence(entity, timestamp, permanenceTimeDistribution.next()))
    override protected def exit(entity: Entity): Structure =
      this.focus(_.entities).modify(_.filter(_.entity != entity))

  /** Builder for the GenericBuilding type of structure
    * @param infectionProbability
    *   the probability of infection inside the structure
    * @param capacity
    *   the capacity of the structure in terms of the number of entities that can enter
    * @param permanenceTimeDistribution
    *   gaussian distribution that describe the permanence time
    * @param entranceStrategy
    *   the strategy used for discriminate the entities entrance
    * @param entities
    *   the entities that are inside the structure.
    * @param isOpen
    *   opening status of the structure
    * @param position
    *   the position of the structure
    * @param visibilityDistance
    *   the distance within the structure is visible for an entity
    * @param group
    *   the group of the structure
    */
  case class GenericBuilding(
      override val position: Point2D,
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime = defaultPermanenceTimeDistribution,
      override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy(),
      override val entities: Set[EntityPermanence] = Set(),
      override val isOpen: Boolean = true,
      override val visibilityDistance: Distance = defaultVisibilityDistance,
      override val group: String = defaultGroup
  ) extends BaseStructure
      with Closable
      with Visible
      with Groupable:
    override type Group = String
    override protected def enter(entity: String, timestamp: TimeStamp): Structure =
      this.focus(_.entities).modify(_ + EntityPermanence(entity, timestamp, permanenceTimeDistribution.next()))
    override protected def exit(entity: Entity): Structure =
      this.focus(_.entities).modify(_.filter(_.entity != entity))

  /** Builder for the GenericBuilding type of structure
    * @param infectionProbability
    *   the probability of infection inside the structure
    * @param capacity
    *   the capacity of the structure in terms of the number of entities that can enter
    * @param permanenceTimeDistribution
    *   gaussian distribution that describe the permanence time
    * @param entranceStrategy
    *   the strategy used for discriminate the entities entrance
    * @param entities
    *   the entities that are inside the structure.
    * @param position
    *   the position of the structure
    * @param visibilityDistance
    *   the distance within the structure is visible for an entity
    * @param treatmentQuality
    *   the virus treatment quality.
    */
  case class Hospital(
      override val position: Point2D,
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime = defaultPermanenceTimeDistribution,
      override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy(),
      override val entities: Set[EntityPermanence] = Set(),
      override val visibilityDistance: Distance = defaultVisibilityDistance,
      override val treatmentQuality: TreatmentQuality
  ) extends BaseStructure
      with Visible
      with Hospitalization:
    override protected def enter(entity: String, timestamp: TimeStamp): Structure =
      this.focus(_.entities).modify(_ + EntityPermanence(entity, timestamp, permanenceTimeDistribution.next()))
    override protected def exit(entity: Entity): Structure =
      this.focus(_.entities).modify(_.filter(_.entity != entity))

package it.unibo.pps.entity.structure

import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import it.unibo.pps.entity.common.Space.{Distance, Point2D}
import it.unibo.pps.entity.structure.StructureComponent.*
import it.unibo.pps.entity.structure.entrance.Entrance.EntranceStrategy
import it.unibo.pps.entity.structure.entrance.Entrance.BaseEntranceStrategy
import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import scala.concurrent.duration.MINUTES
import monocle.syntax.all._

/** This module contains the base structure description and the main implementation that are useful during the
  * simulation configuration
  */
object Structures:
  private val defaultVisibilityDistance = 2
  private val defaultGroup = "base"
  private val defaultPermanenceTimeDistribution = GaussianDurationTime(20, 5, MINUTES)

  /** It's the base [[Structure]] */
  trait BaseStructure extends Structure:
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
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime = defaultPermanenceTimeDistribution,
      override val entities: Set[String] = Set()
  ) extends BaseStructure
      with Habitable:
    override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy()
    override protected def enter(entity: String): Structure =
      this.focus(_.entities).modify(_ + entity)

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
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime = defaultPermanenceTimeDistribution,
      override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy(),
      override val entities: Set[String] = Set(),
      override val isOpen: Boolean = true,
      override val position: Point2D,
      override val visibilityDistance: Distance = defaultVisibilityDistance,
      override val group: String = defaultGroup
  ) extends BaseStructure
      with Closable
      with Placeable
      with Groupable:
    override type Position = Point2D
    override type Group = String
    override protected def enter(entity: String): Structure =
      this.focus(_.entities).modify(_ + entity)

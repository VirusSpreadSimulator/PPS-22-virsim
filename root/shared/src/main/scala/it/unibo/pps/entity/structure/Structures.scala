package it.unibo.pps.entity.structure

import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import it.unibo.pps.entity.common.Space.{Distance, Point2D}
import it.unibo.pps.entity.structure.StructureComponent.*
import it.unibo.pps.entity.structure.entrance.Entrance.EntranceStrategy
import it.unibo.pps.entity.structure.entrance.Entrance.BaseEntranceStrategy
import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime

import scala.concurrent.duration.MINUTES

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
    override type Entity = String // todo: refactor after @accursi's entity ready
    override type TimeDistribution = GaussianDurationTime

  case class House(
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime = defaultPermanenceTimeDistribution,
      override val entities: Set[String] = Set(),
      override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy()
  ) extends BaseStructure
      with Habitable

  case class GenericBuilding(
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime = defaultPermanenceTimeDistribution,
      override val entities: Set[String] = Set(),
      override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy(),
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

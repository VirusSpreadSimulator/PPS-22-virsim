package it.unibo.pps.entity.structure

import it.unibo.pps.entity.common.Space.{Distance, Point2D}
import it.unibo.pps.entity.structure.StructureComponent.*
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization.TreatmentQuality
import it.unibo.pps.entity.structure.entrance.Entrance.{BaseEntranceStrategy, EntranceStrategy, FilterBasedStrategy}
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.entrance.Permanence.EntityPermanence
import it.unibo.pps.control.loader.configuration.SimulationDefaults.StructuresDefault
import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import monocle.syntax.all.*

/** This module contains the base simulation structure description and the main implementations that are useful during
  * the simulation configuration.
  */
object Structures:

  /** It's the simulation [[Structure]]. It specifies all the types, connecting them to the other types in the simulator
    */
  trait SimulationStructure extends Structure:
    override type Position = Point2D
    override type Probability = Double
    override type TimeDistribution = GaussianDurationTime
    override type BaseEntity = SimulationEntity
    override type StrategyToEnter = EntranceStrategy
    override type EntityInStructure = EntityPermanence
    override type SimulationTime = TimeStamp
    override type BaseStructure = SimulationStructure

    /** Method to allow to update the state of internal entities.
      * @param f
      *   the function that update the state. The function returns an [[Option]]. If the option is [[None]] then the
      *   entity will exit from the structure.
      * @return
      *   The modified instance of the structure with the entities updated
      */
    def updateEntitiesInside(f: SimulationEntity => Option[SimulationEntity]): SimulationStructure
    override protected def checkEnter(entity: BaseEntity): Boolean =
      entities.size < capacity && entranceStrategy.canEnter(entity)
    override protected def notEntered(entity: SimulationEntity, timeStamp: TimeStamp): SimulationStructure = this

  /** Builder for the House type of structure.
    * @param position
    *   the position of the house
    * @param infectionProbability
    *   the probability of infection inside the structure
    * @param capacity
    *   the capacity of the structure in terms of the number of entities that can enter
    * @param permanenceTimeDistribution
    *   gaussian distribution that describe the permanence time
    * @param entities
    *   the entities that are inside the structure.
    * @param visibilityDistance
    *   the distance within which the house is visible by the entities.
    */
  case class House(
      override val position: Point2D,
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime =
        StructuresDefault.DEFAULT_HOUSE_PERMANENCE_TIME_DISTRIBUTION,
      override val entities: Set[EntityPermanence] = Set(),
      override val visibilityDistance: Distance = StructuresDefault.DEFAULT_HOUSE_VISIBILITY_DISTANCE
  ) extends SimulationStructure
      with Habitable
      with Visible:
    override val entranceStrategy: EntranceStrategy = new BaseEntranceStrategy()
      with FilterBasedStrategy(_.homePosition == this.position)
    override protected def enter(entity: SimulationEntity, timestamp: TimeStamp): SimulationStructure =
      this.focus(_.entities).modify(_ + EntityPermanence(entity, timestamp, permanenceTimeDistribution.next()))
    override protected def exit(entity: SimulationEntity): SimulationStructure =
      this.focus(_.entities).modify(_.filter(_.entity != entity))
    override def updateEntitiesInside(f: SimulationEntity => Option[SimulationEntity]): SimulationStructure =
      this.focus(_.entities).replace(Utils.updatePermanences(f, this.entities))

  /** Builder for the GenericBuilding type of structure.
    * @param position
    *   the position of the building
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
    * @param visibilityDistance
    *   the distance within the structure is visible for an entity
    * @param group
    *   the group of the structure
    */
  case class GenericBuilding(
      override val position: Point2D,
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime =
        StructuresDefault.DEFAULT_PERMANENCE_TIME_DISTRIBUTION,
      override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy(),
      override val entities: Set[EntityPermanence] = Set(),
      override val isOpen: Boolean = true,
      override val visibilityDistance: Distance = StructuresDefault.DEFAULT_VISIBILITY_DISTANCE,
      override val group: String = StructuresDefault.DEFAULT_GROUP
  ) extends SimulationStructure
      with Closable
      with Visible
      with Groupable:
    override type Group = String
    override protected def enter(entity: SimulationEntity, timestamp: TimeStamp): SimulationStructure =
      this.focus(_.entities).modify(_ + EntityPermanence(entity, timestamp, permanenceTimeDistribution.next()))
    override protected def exit(entity: SimulationEntity): SimulationStructure =
      this.focus(_.entities).modify(_.filter(_.entity != entity))
    override def updateEntitiesInside(f: SimulationEntity => Option[SimulationEntity]): SimulationStructure =
      this.focus(_.entities).replace(Utils.updatePermanences(f, this.entities))

  /** Builder for the Hospital type of structure.
    * @param position
    *   the position of the hospital
    * @param infectionProbability
    *   the probability of infection inside the structure
    * @param capacity
    *   the capacity of the structure in terms of the number of entities that can enter
    * @param permanenceTimeDistribution
    *   gaussian distribution that describe the permanence time
    * @param entranceStrategy
    *   the strategy used to discriminate the entities entrance
    * @param entities
    *   the entities that are inside the structure.
    * @param visibilityDistance
    *   the distance within which the structure is visible by an entity
    * @param treatmentQuality
    *   the virus treatment quality
    */
  case class Hospital(
      override val position: Point2D,
      override val infectionProbability: Double,
      override val capacity: Int,
      override val permanenceTimeDistribution: GaussianDurationTime =
        StructuresDefault.DEFAULT_PERMANENCE_TIME_DISTRIBUTION,
      override val entranceStrategy: EntranceStrategy = BaseEntranceStrategy(),
      override val entities: Set[EntityPermanence] = Set(),
      override val visibilityDistance: Distance = StructuresDefault.DEFAULT_VISIBILITY_DISTANCE,
      override val treatmentQuality: TreatmentQuality = TreatmentQuality.MEDIUM
  ) extends SimulationStructure
      with Visible
      with Hospitalization:
    override protected def enter(entity: SimulationEntity, timestamp: TimeStamp): SimulationStructure =
      this.focus(_.entities).modify(_ + EntityPermanence(entity, timestamp, permanenceTimeDistribution.next()))
    override protected def exit(entity: SimulationEntity): SimulationStructure =
      this.focus(_.entities).modify(_.filter(_.entity != entity))
    override def updateEntitiesInside(f: SimulationEntity => Option[SimulationEntity]): SimulationStructure =
      this.focus(_.entities).replace(Utils.updatePermanences(f, this.entities))

  private object Utils:
    /** Method to update a set of [[EntityPermanence]] based on a function f.
      * @param f
      *   the function that will modify the entities
      * @param ps
      *   the set of [[EntityPermanence]]
      * @return
      *   the modified set of [[EntityPermanence]]
      */
    def updatePermanences(
        f: SimulationEntity => Option[SimulationEntity],
        ps: Set[EntityPermanence]
    ): Set[EntityPermanence] =
      for
        permanence <- ps
        updatedEntityOption = f(permanence.entity)
        if updatedEntityOption.isDefined
        updatedEntity = updatedEntityOption.get
      yield EntityPermanence(updatedEntity, permanence.timestamp, permanence.permanenceDuration)

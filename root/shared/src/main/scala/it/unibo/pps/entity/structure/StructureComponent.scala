package it.unibo.pps.entity.structure

import it.unibo.pps.entity.common.Space.Distance

/** Module that define all the component and the characteristic that can define a Structure in the simulation. */
object StructureComponent:
  /** The Structure base interface. It abstract from all the types because it represent the pure concept of a structure
    * abstracting away from the particular implementation types
    */
  trait Structure:
    type Position
    type Probability
    type TimeDistribution
    type BaseEntity
    type StrategyToEnter
    type EntityInStructure
    type SimulationTime
    type BaseStructure <: Structure

    /** Being placeable, the structure has a position.
      * @return
      *   the position of the structure.
      */
    def position: Position
    /** Each structure has an infection probability that influence the infection of an entity that is inside.
      * @return
      *   the infection probability when an entity is inside the structure.
      */
    def infectionProbability: Probability
    /** Each structure has a capacity in terms of how many entity can be inside.
      * @return
      *   the maximum number of entity contained by the structure.
      */
    def capacity: Int
    /** Each structure has an permanence time defined by a Gaussian Distribution.
      * @return
      *   the gaussian distribution that describe the permanence time.
      */
    def permanenceTimeDistribution: TimeDistribution
    /** Each structure has associated a strategy that affect the enter of entities
      * @return
      *   the strategy
      */
    def entranceStrategy: StrategyToEnter
    /** @return the entities that are inside the structure. */
    def entities: Set[EntityInStructure]
    /** Method that allow an entity to try to enter inside the structure. Note that an entity could be not allowed to
      * enter based on the characteristics of the structure.
      * @param entity
      *   the entity that want to enter
      * @return
      *   The modified instance of the structure if entered, the same instead
      */
    def tryToEnter(entity: BaseEntity, timestamp: SimulationTime): BaseStructure = checkEnter(entity) match
      case true => enter(entity, timestamp)
      case _ => notEntered(entity, timestamp)
    /** Method that allow an entity to exit from the structure. This method WILL NOT handle the entity position, but it
      * will only remove the entity from internal structures.
      * @param entity
      *   the entity that need to exit
      * @return
      *   The modified instance of the structure without the entity inside.
      */
    def entityExit(entity: BaseEntity): BaseStructure = exit(entity)
    /** Method that check if an entity is allowed to enter
      * @param entity
      *   the entity that want to enter
      * @return
      *   true if it can enter, false instead
      */
    protected def checkEnter(entity: BaseEntity): Boolean
    /** Method that insert the entity inside the structure
      * @param entity
      *   the entity that want to enter
      * @param timestamp
      *   the timestamp in which the entity is entered
      * @return
      *   The modified instance of the structure if entered, the same instead
      */
    protected def enter(entity: BaseEntity, timestamp: SimulationTime): BaseStructure
    /** Method that remove an entity from the structure
      * @param entity
      *   the entity that want to exit
      * @return
      *   the modified instance of the structure without the entity inside.
      */
    protected def exit(entity: BaseEntity): BaseStructure
    /** Method to handle the situation in which an entity can't enter in the structure
      * @param entity
      *   the entity for witch the entering is denied
      * @param timeStamp
      *   the timestamp in which the entity request to enter
      * @return
      *   the resulting structure instance
      */
    protected def notEntered(entity: BaseEntity, timeStamp: SimulationTime): BaseStructure

  /** A mixin that describe a [[Structure]] that can be seen. */
  trait Visible extends Structure:
    /** A placeable structure has a distance within which it is visible.
      * @return
      *   the distance within which the structure is visible.
      */
    def visibilityDistance: Distance

  /** A mixin that describe a [[Structure]] that can be closed. */
  trait Closable extends Structure:
    /** A closeable structure is a structure that can be closed. This method represent the state of the structure in
      * that terms
      * @return
      *   true if it's open, false otherwise
      */
    def isOpen: Boolean
    abstract override protected def checkEnter(entity: BaseEntity): Boolean = isOpen && super.checkEnter(entity)

  /** A decoration for structure that can be grouped. */
  trait Groupable:
    structure: Structure =>
    type Group
    /** @return the group to which the structure belongs. */
    def group: Group

  /** A decoration for structure that can be inhabited. */
  trait Habitable:
    structure: Structure =>

  /** A decoration for structure that can provide treatment for the virus. */
  trait Hospitalization:
    structure: Structure =>
    import Hospitalization.TreatmentQuality
    /** Each hospital has a virus treatment quality.
      * @return
      *   the virus treatment quality.
      */
    def treatmentQuality: TreatmentQuality

  object Hospitalization:
    /** Describe the quality of the treatment provided by the structure. */
    enum TreatmentQuality:
      case GOOD, MEDIUM, LOW

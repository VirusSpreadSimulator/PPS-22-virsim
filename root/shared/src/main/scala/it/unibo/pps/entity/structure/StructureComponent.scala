package it.unibo.pps.entity.structure

import it.unibo.pps.entity.common.Space.Distance
import it.unibo.pps.entity.structure.entrance.Entrance.EntranceStrategy

/** Module that define all the component and the characteristic that can define a Structure in the simulation. */
object StructureComponent:
  /** The Structure base interface. */
  trait Structure:
    type Probability
    type Entity
    type TimeDistribution

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
    def entranceStrategy: EntranceStrategy
    /** @return the entity that are inside the structure. */
    def entities: Set[Entity]

  /** A [[Structure]] that can be placed. */
  trait Placeable extends Structure:
    structure: Structure =>
    type Position
    /** Being placeable, the structure has a position.
      * @return
      *   the position of the structure.
      */
    def position: Position
    /** A placeable structure has a distance within which it is visible.
      * @return
      *   the distance within which the structure is visible.
      */
    def visibilityDistance: Distance

  /** A [[Structure]] that can be closed. It will affect the tryToEnter method. */
  trait Closable:
    structure: Structure =>
    def isOpen: Boolean

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
  trait Hospital:
    structure: Structure =>
    import Hospital.TreatmentQuality
    def treatmentQuality: TreatmentQuality

  object Hospital:
    /** Describe the quality of the treatment provided by the structure. */
    enum TreatmentQuality:
      case GOOD, MEDIUM, LOW


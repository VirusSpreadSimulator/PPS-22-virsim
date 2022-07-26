package it.unibo.pps.entity.entity

/* A module that represents the characteristics that can have an Entity in the simulation. */
object EntityComponent:
  /* Base implementation of an entity. */
  trait Entity:
    type Position

    /** Uniquely defines the entity
      * @return
      */
    def id: Int

    /** The age of the entity.
      * @return
      *   the age of the entity.
      */
    def age: Int

    /** Every entity is assigned to an habitable Structure, the entity store the position of that home
      * @return
      *   the home position to which the entity is assigned.
      */
    def homePosition: Position

  /* Represent the an entity that exist in the simulation and can move*/
  trait Moving extends Entity:

    /** The current position of the entity, it can be a position in the grid or a structure.
      * @return
      *   the current position of the entity.
      */
    def position: Position

    import Moving.MovementGoal
    /** Define the goal of the movement
      * @return
      */
    def movementGoal: MovementGoal

  object Moving:
    /** Describe the movement of the entity */
    enum MovementGoal:
      case RANDOM_MOVEMENT, BACK_TO_HOME, NO_MOVEMENT

  /* Represent an Entity that could be infected by another entity. */
  trait Infectious extends Entity:
    /** An entity could be infected by the virus
      * @return
      *   An object Some containing the infection if present, None otherwise.
      */
    def infection: Option[Infection]

  /* Represent an entity that can wear a mask to protect against virus*/
  trait Masquerable extends Entity:
    /** During the simulation, an entity could wear a mask to decrease the probability of an infection
      * @return
      *   true if the entity has a mask, false otherwise
      */
    def hasMask: Boolean

  /*Represent an entity that could have an immunity rate that protect against virus*/
  trait Immune extends Entity:
    /** Every entity could have an immunity rate, that increase with a vaccine or after an infection and decrease as the
      * simulation progresses
      * @return
      *   the current immunity rate of the entity
      */
    def immunity: Double

  trait Living extends Infectious with Immune:
    /** The current health of an entity.
      * @return
      *   the health of the entity
      */
    def health: Double

    /** Every entity has a personalized maximum health status, base on the age.
      * @return
      *   the maximum health status that the entity could have.
      */
    def maxHealth: Double

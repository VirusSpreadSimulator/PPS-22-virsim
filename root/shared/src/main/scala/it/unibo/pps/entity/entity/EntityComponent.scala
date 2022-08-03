package it.unibo.pps.entity.entity

import it.unibo.pps.entity.common.Space.Point2D

/* A module that represents the characteristics that can have an Entity in the simulation. */
object EntityComponent {
  /* Base implementation of an entity. */
  trait Entity:
    type Home
    type Position

    /** The age of the entity.
      * @return
      *   the age of the entity.
      */
    def age: Int

    /** Every entity is assigned to an habitable Structure.
      * @return
      *   the home to which the entity is assigned.
      */
    def home: Home

    /** The current health of an entity.
      * @return
      *   the healt of the entity
      */
    def health: Int

    /** Every entity has a personalized maximum health status, base on the age.
      * @return
      *   the maximum health status that the entity could have.
      */
    def maxHealth: Int

    /** Every entity could have an immunity rate, that increase with a vaccine or after an infection and decrease as the
      * simulation progresses
      * @return
      *   the current immunity rate of the entity
      */
    def immunity: Int

  /* Represent the an entity that exist in the simulation and can move*/
  trait Moving extends Entity:

    /** The current position of the entity, it can be a position in the grid or a structure.
      * @return
      *   the current position of the entity.
      */
    def position: Point2D

    import Moving.movementGoal
    /** Define the goal of the movement
      * @return
      */
    def movementGoal: movementGoal

  object Moving:
    /** Describe the movement of the entity */
    enum movementGoal:
      case RANDOM_MOVEMENT, BACK_TO_HOME, NO_MOVEMENT

  /* Represent an Entity that could be infected by another entity. */
  trait Infected extends Entity:
    /** An entity could be infected by the virus
      * @return
      *   An object Some containing the infection if present, None otherwise.
      */
    def infection: Infection

}

package it.unibo.pps.entity.entity

/* A module that represents the characteristics that can have an Entity in the simulation. */
object EntityComponent {
  /* Represent the infection of an entity. */
  case class Infection(severity: Int, infectionDuration: Int)

  /* Base implementation of an entity. */
  trait BaseEntity:
    type Home
    type Position

    /** The age of the entity.
      * @return
      *   the age of the entity.
      */
    def age: Int

    /** Every entity is assigned to an habitble Structure.
      * @return
      *   the home to which the entity is assigned.
      */
    def home: Home

    /** The current position of the entity, it can be a position in the grid or a structure.
      * @return
      *   the current position of the entity.
      */
    def position: Position

    /** An entity could be infected by the virus
      * @return
      *   An object Some containing the infection if present, None otherwise.
      */
    def infection: Option[Infection]

  /* Represent the an entity that can live or Die*/
  trait Living extends BaseEntity:
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

  /* Represent an Entity that could have an immunity rate to the virus. */
  trait Immune extends BaseEntity:
    /** Every entity could have an immunity rate, that increase with a vaccine or after an infection and decrease as the
      * simulation progresses
      * @return
      *   the current immunity rate of the entity
      */
    def immunity: Int
}

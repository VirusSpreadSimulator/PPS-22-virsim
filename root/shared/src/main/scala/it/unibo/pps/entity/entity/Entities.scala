package it.unibo.pps.entity.entity

import it.unibo.pps.entity.entity.EntityComponent.*
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.structure.Structures.*
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal

object Entities:
  /* The base entity of the simulation. */
  trait BaseEntity extends Entity:
    override type Position = Point2D
    override type Home = House

    /** Calculates the max health that an entity can have during the simulation
      * @return
      *   the max health for an entity
      */
    def calculateMaxHealth(): Int = (100 - (30.0 / 100.0) * age).toInt //to be refactored removing magic numbers

  /** Case class for the entity of the simulation.
    * @param id
    *   the id of the entity.
    * @param age
    *   the age of the entity.
    * @param home
    *   the home of the entity
    * @param immunity
    *   the immunity rate to the infection
    * @param position
    *   current position of the entity
    * @param movementGoal
    *   the goal of the movement of the entity
    * @param infection
    *   if present, describes the infection of the entity
    */
  case class SimulationEntity(
      override val id: Int,
      override val age: Int,
      override val home: House,
      override val immunity: Int = 0,
      override val position: Point2D,
      override val movementGoal: MovementGoal = MovementGoal.RANDOM_MOVEMENT,
      override val infection: Option[Infection] = None
  ) extends BaseEntity
      with Moving
      with Infectious:

    /* max health that the entity can have, based on the age. */
    override val maxHealth: Int = calculateMaxHealth()
    /* current health of the entity. Initially set to max health.  */
    override val health: Int = maxHealth
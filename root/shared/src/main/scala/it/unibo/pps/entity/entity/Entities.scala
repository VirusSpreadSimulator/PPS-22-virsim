package it.unibo.pps.entity.entity

import it.unibo.pps.entity.entity.EntityComponent.*
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.structure.Structures.*
import it.unibo.pps.entity.entity.EntityComponent.Moving.movementGoal

object Entities:
  /* The base entity of the simulation. */
  trait BaseEntity extends Entity:
    override type Position = Point2D
    override type Home = House

  /** Case class for the entity of the simulation.
    * @param id
    *   the id of the entity.
    * @param age
    *   the age of the entity.
    * @param home
    *   the home of the entity
    * @param health
    *   current health of the entity
    * @param maxHealth
    *   max health that the entity can have.
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
      override val movementGoal: movementGoal,
      override val infection: Option[Infection]
  ) extends BaseEntity
      with Moving
      with Infectious:

    override val maxHealth: Int = (100 - (30.0 / 100.0) * age).toInt // TO BE REFACTORED
    override val health: Int = maxHealth

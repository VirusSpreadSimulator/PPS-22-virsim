package it.unibo.pps.entity.entity

import it.unibo.pps.control.loader.configuration.SimulationDefaults
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.EntityComponent.*
import it.unibo.pps.entity.entity.EntityComponent.Moving.MovementGoal
import it.unibo.pps.entity.structure.Structures.*

object Entities:
  /** Case class for the entity of the simulation.
    * @param id
    *   the id of the entity.
    * @param age
    *   the age of the entity.
    * @param homePosition
    *   the position of the home of the entity
    * @param health
    *   the entity's health
    * @param immunity
    *   the immunity rate to the infection
    * @param position
    *   current position of the entity
    * @param movementGoal
    *   the goal of the movement of the entity
    * @param infection
    *   if present, describes the infection of the entity
    * @param hasMask
    *   describe if the entity is wearing a mask
    */
  case class SimulationEntity(
      override val id: Int,
      override val age: Int,
      override val homePosition: Point2D,
      override val health: Double,
      override val immunity: Double = 0.0,
      override val position: Point2D,
      override val movementGoal: MovementGoal = MovementGoal.RANDOM_MOVEMENT,
      override val infection: Option[Infection] = None,
      override val hasMask: Boolean = false
  ) extends Entity
      with Moving
      with Masquerable
      with Living:

    import it.unibo.pps.entity.entity.Entities.SimulationEntity.calculateMaxHealth

    override type Position = Point2D
    /* max health that the entity can have, based on the age. */
    override val maxHealth: Double = SimulationEntity.calculateMaxHealth(age)

    /* redefine the equals method, two entities are the same if they have the same id */
    override def equals(that: Any): Boolean = that match
      case that: SimulationEntity => that.id == this.id
      case _ => false

  object SimulationEntity:
    private def calculateMaxHealth(age: Int): Double =
      import it.unibo.pps.control.loader.configuration.SimulationDefaults
      HealthCalculator.calculateMaxHealth(age)

    def apply(
        id: Int,
        age: Int,
        homePosition: Point2D,
        health: Double,
        immunity: Double = 0.0,
        position: Point2D,
        movementGoal: MovementGoal = MovementGoal.RANDOM_MOVEMENT,
        infection: Option[Infection] = None,
        hasMask: Boolean = false
    ): SimulationEntity =
      new SimulationEntity(
        id,
        Math.max(0, age),
        homePosition,
        Math.max(SimulationDefaults.MIN_VALUES.MIN_HEALTH, Math.min(health, calculateMaxHealth(age))),
        immunity,
        position,
        movementGoal,
        infection,
        hasMask
      )

  object HealthCalculator:
    import scalaz.Memo
    val calculateMaxHealth: Int => Double = Memo.immutableHashMapMemo { age =>
      SimulationDefaults.MAX_VALUES.MAX_HEALTH - ((SimulationDefaults.MAX_VALUES.MAX_HEALTH - SimulationDefaults.MIN_VALUES.MIN_INITIAL_HEALTH.toDouble) / SimulationDefaults.MAX_VALUES.MAX_HEALTH) * Math
        .max(0, age)
    }

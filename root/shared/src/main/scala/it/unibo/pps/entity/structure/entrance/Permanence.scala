package it.unibo.pps.entity.structure.entrance

import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.Entities.SimulationEntity

/** Module that contains the permanence-related concepts. */
object Permanence:
  /** Model the permanence status. */
  enum PermanenceStatus:
    /** VALID: permanence still in its assigned duration time. */
    case VALID
    /** OVER: permanence over its assigned duration time. */
    case OVER

  /** Model the permanence of an entity inside a structure. */
  trait EntityPermanence:
    /** The entity that is inside the structure.
      * @return
      *   the entity
      */
    def entity: SimulationEntity
    /** The timestamp that represent the entry time of the entity inside the structure.
      * @return
      *   the timestamp
      */
    def timestamp: TimeStamp
    /** The permanence time inside the structure.
      * @return
      *   the duration time
      */
    def permanenceDuration: DurationTime
    /** Considering the timestamp in which the entity is entered and the permanence duration time is possible to
      * understand if the entity is allowed to stay inside or it needs to exit.
      * @param timestampToCheck
      *   the timestamp respect to which perform the check.
      * @return
      *   a [[PermanceStatus]] that represent the status of the permanence.
      */
    def status(timestampToCheck: TimeStamp): PermanenceStatus

  object EntityPermanence:
    /** @param entity
      *   the entity that enters in the structure
      * @param timestamp
      *   the entry-time timestamp
      * @param permanenceDuration
      *   the duration of the permanence
      * @return
      *   the [[EntityPermanence]]
      */
    def apply(entity: SimulationEntity, timestamp: TimeStamp, permanenceDuration: DurationTime): EntityPermanence =
      EntityPermanenceImpl(entity, timestamp, permanenceDuration)
    private case class EntityPermanenceImpl(
        entity: SimulationEntity,
        timestamp: TimeStamp,
        permanenceDuration: DurationTime
    ) extends EntityPermanence:
      override def status(timeStampToCheck: TimeStamp): PermanenceStatus =
        timeStampToCheck <= timestamp + permanenceDuration match
          case true => PermanenceStatus.VALID
          case _ => PermanenceStatus.OVER

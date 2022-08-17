package it.unibo.pps.entity.structure.entrance

import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityComponent.Entity

object Permanence:
  /** Model the permanence status */
  enum PermanenceStatus:
    /** VALID: permanence still in its assigned duration time */
    case VALID
    /** OVER: permance over its assigned duration time */
    case OVER

  trait EntityPermanence:
    /** The entity that is inside the structure
      * @return
      *   the entity
      */
    def entity: SimulationEntity
    /** The timestamp that represent the entry time of the entity in the structure
      * @return
      *   the timestamp
      */
    def timestamp: TimeStamp
    /** The permanence time inside the structure
      * @return
      *   the duration time
      */
    def permanenceDuration: DurationTime
    /** Considering the timestamp in witch the entity is entered and the permanence duration time is possibile to
      * understand if the entity is allowed to stay inside or it need to exit.
      * @param timestampToCheck
      *   the timestamp respect witch doing the check.
      * @return
      *   a [[PermanceStatus]] that represent the status of the permanence.
      */
    def status(timestampToCheck: TimeStamp): PermanenceStatus

  object EntityPermanence:
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

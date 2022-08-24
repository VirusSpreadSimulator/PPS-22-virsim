package it.unibo.pps.entity.entity

import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.Infection.Severity

/*Represent the infection of an entity*/
trait Infection:

  /** Represent the severity of the infection.
    * @return
    *   the severity of the infection
    */
  def severity: Severity

  /** Represents when the infection occurred.
    * @return
    *   the timestamp of when the infection occurred
    */
  def timeOfTheInfection: TimeStamp

  /** Represent the duration of the infection.
    * @return
    *   the duration of the infection
    */
  def duration: DurationTime

object Infection:

  enum Severity:
    case SERIOUS()
    case LIGHT()
    def value: Double = this match
      case SERIOUS() => 1.8
      case _ => 1.2

  /** Factory for infection
    * @param severity
    *   the severity of the infection
    * @param time
    *   the time when infection occurred
    * @param duration
    *   the duration of the infection
    * @return
    */
  def apply(severity: Severity, time: TimeStamp, duration: DurationTime): Infection =
    InfectionImpl(severity, time, duration)
  private class InfectionImpl(
      override val severity: Severity,
      override val timeOfTheInfection: TimeStamp,
      override val duration: DurationTime
  ) extends Infection

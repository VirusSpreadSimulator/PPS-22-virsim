package it.unibo.pps.entity.entity

import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.Infection.Severity

trait Infection:
  def severity: Severity
  def timeOfTheInfection: TimeStamp
  def duration: DurationTime

object Infection:

  enum Severity:
    case SERIOUS()
    case LIGHT()
    def value: Double = this match
      case SERIOUS() => 1.8
      case _ => 1.2

  def apply(severity: Severity, time: TimeStamp, duration: DurationTime): Infection =
    InfectionImpl(severity, time, duration)
  private class InfectionImpl(
      override val severity: Severity,
      override val timeOfTheInfection: TimeStamp,
      override val duration: DurationTime
  ) extends Infection

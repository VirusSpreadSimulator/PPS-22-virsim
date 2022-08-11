package it.unibo.pps.entity.entity

import it.unibo.pps.entity.entity.Infection.Severity

trait Infection:
  def severity: Severity
  def daysOfDurationOfInfection: Int
  def remainingDaysOfInfection: Int

object Infection:

  enum Severity:
    case SERIOUS(value: Double = 1.8)
    case LIGHT(value: Double = 1.2)

  def apply(severity: Severity, duration: Int): Infection =
    InfectionImpl(severity, duration, duration)
  private class InfectionImpl(
      override val severity: Severity,
      override val daysOfDurationOfInfection: Int,
      override val remainingDaysOfInfection: Int
  ) extends Infection

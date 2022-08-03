package it.unibo.pps.entity.entity

trait Infection:
  def severity: Int
  def daysOfDurationOfInfection: Int
  def remainingDaysOfInfection: Int

object Infection:
  def apply(severity: Int, duration: Int): Infection =
    InfectionImpl(severity, duration, duration)
  private class InfectionImpl(
      override val severity: Int,
      override val daysOfDurationOfInfection: Int,
      override val remainingDaysOfInfection: Int
  ) extends Infection

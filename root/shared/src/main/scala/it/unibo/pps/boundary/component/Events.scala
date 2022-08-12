package it.unibo.pps.boundary.component

import it.unibo.pps.entity.environment.EnvironmentStatus

object Events:
  /** Event represents all the event that can be fired by the user interface. */
  enum Event:
    case Hit(number: Int) // todo: delete
    /** The user signal the intent to pause the simulation. */
    case Pause
    /** The user signal the intent to resume the simulation. */
    case Resume
    /** The user signal the intent to stop the simulation */
    case Stop
    /** The user signal the intent to change the speed of the simulation
      * @param speed
      *   the selected speed
      */
    case ChangeSpeed(speed: Params.Speed)
    /** The user signal the intent to switch the obligation of the mask */
    case SwitchMaskObligation
    /** The user signal the intent to do a vaccine round.
      * @param percentage
      *   the percentage of entities that will be vaccinated
      */
    case VaccineRound(percentage: Double)
    /** The user signal the intent to open/close a structure
      * @param group:
      *   the group to open/close
      */
    case SwitchStructure(group: String)

    private def interestedStatus: Set[EnvironmentStatus] = this match
      case Event.Resume | Event.Stop | Event.ChangeSpeed(_) => Set(EnvironmentStatus.EVOLVING, EnvironmentStatus.PAUSED)
      case _ => Set(EnvironmentStatus.EVOLVING)

    def interested(environmentStatus: EnvironmentStatus): Boolean = this.interestedStatus contains environmentStatus

  object Params:
    enum Speed:
      case SLOW, NORMAL, FAST

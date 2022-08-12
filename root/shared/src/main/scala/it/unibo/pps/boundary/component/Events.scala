package it.unibo.pps.boundary.component

import it.unibo.pps.control.engine.config.Configurations.EngineStatus

object Events:
  /** Event represents all the event that can be fired by the user interface. */
  enum Event:
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

    private def interestedStatus: Set[EngineStatus] = this match
      case Event.Resume | Event.Stop | Event.ChangeSpeed(_) => Set(EngineStatus.RUNNING, EngineStatus.PAUSED)
      case _ => Set(EngineStatus.RUNNING)

    def interested(engineStatus: EngineStatus): Boolean = this.interestedStatus contains engineStatus

  object Params:
    enum Speed:
      case SLOW, NORMAL, FAST

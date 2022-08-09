package it.unibo.pps.boundary.component

object Events:
  /** Event represents all the event that can be fired by the user interface. */
  enum Event:
    case Hit(number: Int) // todo: delete
    /** The user signal the intent to pause the simulation. */
    case Pause
    /** The user signal the intent to stop the simulation */
    case Stop
    /** The user signal the intent to switch the obligation of the mask */
    case SwitchMaskObligation
    /** The user signal the intent to open/close a structure
      * @param group:
      *   the group to open/close
      */
    case SwitchStructure(group: String)

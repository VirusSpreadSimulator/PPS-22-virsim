package it.unibo.pps.control.engine.config

import it.unibo.pps.boundary.component.Events
import scala.concurrent.duration.FiniteDuration
import concurrent.duration.DurationInt

/** This module contains all the possible configurations concepts */
object Configurations:
  /** Models the speed of the simulation engine */
  enum EngineSpeed:
    case SLOW
    case NORMAL
    case FAST

    /** Obtain the tick time of the engine
      * @return
      *   a [[FiniteDuration]] that express the tick time
      */
    def tickTime: FiniteDuration = this match
      case SLOW => 200.millis
      case NORMAL => 100.millis
      case FAST => 50.millis

  object EngineSpeed:
    /** Conversion from the boundary event parameter to the actual speed concept
      * @param speed
      *   the speed event parameter
      * @return
      *   the actual [[EngineSpeed]]
      */
    def fromEvent(speed: Events.Params.Speed): EngineSpeed = speed match
      case Events.Params.Speed.SLOW => SLOW
      case Events.Params.Speed.NORMAL => NORMAL
      case Events.Params.Speed.FAST => FAST

  /** It represent the current status of the engine of the simulation */
  enum EngineStatus:
    /** [[EngineStatus.RUNNING]] means that the simulation is currently running */
    case RUNNING
    /** [[EngineStatus.PAUSED]] means that the simulation engine is in a paused state, no event logics, no updates will
      * be computed on it
      */
    case PAUSED
    /** [[EngineStatus.STOPPED]] means that the simulation is terminated, the environment is no more evolving in a
      * definitive way
      */
    case STOPPED

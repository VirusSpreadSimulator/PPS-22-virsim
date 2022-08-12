package it.unibo.pps.entity.environment

/** It represent the current status of the environment */
enum EnvironmentStatus:
  /** [[Evolving]] means that is currently under update from the engine */
  case EVOLVING
  /** [[Pause]] means that the environment is in a paused state, no event logics, no updates will be computed on it */
  case PAUSED
  /** [[Stop]] means that the environment is no more evolving in a definitive way */
  case STOPPED

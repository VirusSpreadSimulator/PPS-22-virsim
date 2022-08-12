package it.unibo.pps.control.engine.behaviouralLogics

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task

/** Module that wrap all the logic types that are needed to update the simulation [[Environment]] */
object Logic:
  /** Update logic represent a generic logic that is performed at each iteration. It takes the current environment and
    * return a task that represent the computation done on that environment
    */
  type UpdateLogic = Environment => Task[Environment]

  object UpdateLogic:
    /** Identity logic */
    def identity: UpdateLogic = Task(_)

  /** Update logic represent a logic that is associated to an event. It takes the current environment and return a task
    * that represent the computation done on that environment due to the occur of the event.
    */
  type EventLogic = Environment => Task[Environment]

  object EventLogic:
    /** Identity logic */
    def identity: EventLogic = Task(_)

package it.unibo.pps.control.engine.behaviouralLogics

import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task

/** Module that wrap all the logic traits that are needed to update the simulation [[Environment]] */
object Logic:
  /** Update logic represent a generic logic that is performed at each iteration */
  trait UpdateLogic:
    /** Execute the logic
      * @param env
      *   the current environment
      * @return
      *   a task that represent the computation
      */
    def execute(env: Environment): Task[Environment]

  object UpdateLogic:
    /** Identity logic */
    def identity: UpdateLogic = Task(_)

  /** Event logic represent a logic that is associated to an event */
  trait EventLogic:
    def handle(env: Environment): Task[Environment]

  object EventLogic:
    /** Identity logic */
    def identity: EventLogic = Task(_)

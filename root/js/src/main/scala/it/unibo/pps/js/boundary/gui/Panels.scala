package it.unibo.pps.js.boundary.gui

import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.eval.Task

object Panels:
  trait UpdatablePanel:
    def init(): Task[Unit]
    def update(newEnv: Environment): Task[Unit]

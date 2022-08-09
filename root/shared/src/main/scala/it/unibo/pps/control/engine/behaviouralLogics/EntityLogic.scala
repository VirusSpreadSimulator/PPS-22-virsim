package it.unibo.pps.control.engine.behaviouralLogics

import it.unibo.pps.entity.environment.EnvironmentModule.Environment

trait EntityLogic:
  def execute(environment: Environment): Environment

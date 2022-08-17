package it.unibo.pps.control.engine.logics

import it.unibo.pps.entity.environment.EnvironmentModule.Environment

trait EntityLogic:
  def execute(environment: Environment): Environment

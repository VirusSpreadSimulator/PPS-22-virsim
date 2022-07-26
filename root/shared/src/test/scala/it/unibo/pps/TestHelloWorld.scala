package it.unibo.pps

import org.scalatest.funsuite.AnyFunSuite
import it.unibo.pps.entity.EnvModule
import it.unibo.pps.entity.State

class TestHelloWorld extends AnyFunSuite:

  class TestEnv extends EnvModule.Interface:
    override val env = EnvImpl()

  test("Test init environment") {
    val environment = TestEnv().env
    assert(environment.getState() === State(0))
  }

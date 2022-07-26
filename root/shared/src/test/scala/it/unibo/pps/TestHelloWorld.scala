package it.unibo.pps

import org.scalatest.funsuite.AnyFunSuite

class TestHelloWorld extends AnyFunSuite:

  test("The project name must be virsim") {
    assert(Person("nome", "cognome").name === "nome")
  }

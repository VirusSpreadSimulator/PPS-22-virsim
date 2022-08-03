package it.unibo.pps.entities

import it.unibo.pps.entity.entity.IdGenerator
import it.unibo.pps.entity.entity.IdGenerator.IntegerIdGenerator
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class IdGeneratorTest extends AnyFunSuite with Matchers:
  private val idGenerator = IntegerIdGenerator(0)
  test("Initially the idGenerator is set at 0") {
    idGenerator.current shouldBe 0
  }

  test("the first call to next should return 1") {
    idGenerator.next() shouldBe 1
  }

package it.unibo.pps.entity.common

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Hospitalization, Structure}
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, House, SimulationStructure}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.common.Utils.*

class UtilsTest extends AnyFunSuite with Matchers:
  private val infectionProbability: Double = 1
  private val capacity: Int = 2
  private val position: Point2D = Point2D(3, 0)
  private val initialSet: Set[SimulationStructure] =
    Set(House(position, infectionProbability, capacity), GenericBuilding(position, infectionProbability, capacity))

  test("Selection on a parent type must be allowed") {
    initialSet.select[Structure].size shouldBe initialSet.size
  }

  test("Selection on a type that is not extended by anyone must return an empty set") {
    initialSet.select[Hospitalization].size shouldBe 0
  }

  test("Selection on a completely different type must return an empty set") {
    initialSet.select[Int].size shouldBe 0
  }

  test("We can extract from a group of structures only the closable ones") {
    initialSet.select[Closable].size shouldBe 1
  }

  test("We can extract a supertype") {
    1.withCapabilities[AnyVal].isDefined shouldBe true
  }

  test("We can extract a subtype") {
    val x: AnyVal = 2
    x.withCapabilities[Int].isDefined shouldBe true
  }

  test("Un-convertible type should be safely handled") {
    1.withCapabilities[Point2D].isEmpty shouldBe true
  }

  test("It's possible to do a computation on an element only if a condition hold") {
    1.andIf(_ == 1)(_ + 1) shouldBe 2
  }

  test("If the condition doesn't hold andIf will not modify anything") {
    1.andIf(_ == 10)(_ + 1) shouldBe 1
  }

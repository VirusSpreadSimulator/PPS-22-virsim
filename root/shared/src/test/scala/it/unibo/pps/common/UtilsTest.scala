package it.unibo.pps.common

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Habitable, Hospitalization, Structure}
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, House, SimulationStructure}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class UtilsTest extends AnyFunSuite with Matchers:
  import it.unibo.pps.entity.common.Utils.*

  val infectionProbability: Double = 1
  val capacity: Int = 2
  val position: Point2D = Point2D(3, 0)
  val initialSet: Set[SimulationStructure] =
    Set(House(position, infectionProbability, capacity), GenericBuilding(position, infectionProbability, capacity))

  test("Selection on a parent type must be allowed") {
    initialSet.select[Structure].size shouldBe 2
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

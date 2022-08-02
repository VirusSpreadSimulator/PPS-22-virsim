package it.unibo.pps.structure.entrance

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House}
import it.unibo.pps.entity.structure.StructureComponent.{Closable, Structure, Hospitalization}
import it.unibo.pps.entity.structure.entrance.Entrance.{BaseEntranceStrategy, FilterBasedStrategy}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import monocle.syntax.all.*

//todo: refactor with correct entities
class StructuresTest extends AnyFunSuite with Matchers:
  private class FilteredStrategyTrue extends BaseEntranceStrategy with FilterBasedStrategy(_.contains("ent"))
  private class FilteredStrategyFalse extends BaseEntranceStrategy with FilterBasedStrategy(_.contains("rand"))
  private val infectionProbability = 2
  private val capacity = 2
  private val entities = Seq("entity1", "entity2", "entity3")
  private val position = Point2D(3, 0)
  private val treatmentQuality = Hospitalization.TreatmentQuality.GOOD

  test("Initially a house is empty") {
    val house: Structure = House(infectionProbability, capacity)
    house.entities.size shouldBe 0
  }

  test("In a house can enter anyone") {
    var house: Structure = House(infectionProbability, capacity)
    house = house.tryToEnter(entities.head)
    house.entities.size shouldBe 1
  }

  test("No more entities than capacity can enter in a House") {
    var house: Structure = House(infectionProbability, capacity)
    house = tryToEnterMultiple(house, entities)
    house.entities.size shouldBe capacity
  }

  test("Initially a generic building is empty") {
    val building: Structure = GenericBuilding(infectionProbability, capacity, position = position)
    building.entities.size shouldBe 0
  }

  test("In a generic building the strategy must be considered, true case") {
    var building: Structure =
      GenericBuilding(infectionProbability, capacity, entranceStrategy = FilteredStrategyTrue(), position = position)
    building = building.tryToEnter(entities.head)
    building.entities.size shouldBe 1
  }

  test("In a generic building the strategy must be considered, false case") {
    var building: Structure =
      GenericBuilding(infectionProbability, capacity, entranceStrategy = FilteredStrategyFalse(), position = position)
    building = building.tryToEnter(entities.head)
    building.entities.size shouldBe 0
  }

  test("A generic building can be closed after creation and will not accept entities") {
    var building = GenericBuilding(infectionProbability, capacity, position = position)
    building = building.focus(_.isOpen).replace(false)
    building.tryToEnter(entities.head).entities.size shouldBe 0
  }

  test("In a generic building even if the strategy allow the entity, the capacity must be respected") {
    var building: Structure =
      GenericBuilding(infectionProbability, capacity, entranceStrategy = FilteredStrategyTrue(), position = position)
    building = tryToEnterMultiple(building, entities)
    building.entities.size shouldBe capacity
  }

  test("Initially a hospital is empty") {
    val hospital: Structure =
      Hospital(infectionProbability, capacity, position = position, treatmentQuality = treatmentQuality)
    hospital.entities.size shouldBe 0
  }

  test("In a hospital the strategy must be considered, true case") {
    var hospital: Structure =
      Hospital(
        infectionProbability,
        capacity,
        entranceStrategy = FilteredStrategyTrue(),
        position = position,
        treatmentQuality = treatmentQuality
      )
    hospital = hospital.tryToEnter(entities.head)
    hospital.entities.size shouldBe 1
  }

  test("In a hospital the strategy must be considered, false case") {
    var hospital: Structure =
      Hospital(
        infectionProbability,
        capacity,
        entranceStrategy = FilteredStrategyFalse(),
        position = position,
        treatmentQuality = treatmentQuality
      )
    hospital = hospital.tryToEnter(entities.head)
    hospital.entities.size shouldBe 0
  }

  test("In a hospital even if the strategy allow the entity, the capacity must be respected") {
    var hospital: Structure =
      Hospital(
        infectionProbability,
        capacity,
        entranceStrategy = FilteredStrategyTrue(),
        position = position,
        treatmentQuality = treatmentQuality
      )
    hospital = tryToEnterMultiple(hospital, entities)
    hospital.entities.size shouldBe capacity
  }

  private def tryToEnterMultiple(structure: Structure, entities: Seq[String]): Structure =
    var s = structure
    for entity <- entities do s = s.tryToEnter(entity)
    s

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
  private val house = House(infectionProbability, capacity)
  private val building = GenericBuilding(infectionProbability, capacity, position = position)
  private val hospital =
    Hospital(infectionProbability, capacity, position = position, treatmentQuality = treatmentQuality)

  test("Initially a house is empty") {
    house.entities.size shouldBe 0
  }

  test("In a house can enter anyone") {
    var houseCopy: Structure = house.copy()
    houseCopy = houseCopy.tryToEnter(entities.head)
    houseCopy.entities.size shouldBe 1
  }

  test("No more entities than capacity can enter in a House") {
    var houseCopy: Structure = house.copy()
    houseCopy = tryToEnterMultiple(houseCopy, entities)
    houseCopy.entities.size shouldBe capacity
  }

  test("Initially a generic building is empty") {
    building.entities.size shouldBe 0
  }

  test("In a generic building the strategy must be considered, true case") {
    var buildingCopy: Structure = building.copy(entranceStrategy = FilteredStrategyTrue())
    buildingCopy = buildingCopy.tryToEnter(entities.head)
    buildingCopy.entities.size shouldBe 1
  }

  test("In a generic building the strategy must be considered, false case") {
    var buildingCopy: Structure = building.copy(entranceStrategy = FilteredStrategyFalse())
    buildingCopy = buildingCopy.tryToEnter(entities.head)
    buildingCopy.entities.size shouldBe 0
  }

  test("A generic building can be closed after creation and will not accept entities") {
    var buildingCopy = building.copy()
    buildingCopy = buildingCopy.focus(_.isOpen).replace(false)
    buildingCopy.tryToEnter(entities.head).entities.size shouldBe 0
  }

  test("In a generic building even if the strategy allow the entity, the capacity must be respected") {
    var buildingCopy: Structure = building.copy(entranceStrategy = FilteredStrategyTrue())
    buildingCopy = tryToEnterMultiple(buildingCopy, entities)
    buildingCopy.entities.size shouldBe capacity
  }

  test("Initially a hospital is empty") {
    hospital.entities.size shouldBe 0
  }

  test("In a hospital the strategy must be considered, true case") {
    var hospitalCopy: Structure = hospital.copy(entranceStrategy = FilteredStrategyTrue())
    hospitalCopy = hospitalCopy.tryToEnter(entities.head)
    hospitalCopy.entities.size shouldBe 1
  }

  test("In a hospital the strategy must be considered, false case") {
    var hospitalCopy: Structure = hospital.copy(entranceStrategy = FilteredStrategyFalse())
    hospitalCopy = hospitalCopy.tryToEnter(entities.head)
    hospitalCopy.entities.size shouldBe 0
  }

  test("In a hospital even if the strategy allow the entity, the capacity must be respected") {
    var hospitalCopy: Structure = hospital.copy(entranceStrategy = FilteredStrategyTrue())
    hospitalCopy = tryToEnterMultiple(hospitalCopy, entities)
    hospitalCopy.entities.size shouldBe capacity
  }

  private def tryToEnterMultiple(structure: Structure, entities: Seq[String]): Structure =
    var s = structure
    for entity <- entities do s = s.tryToEnter(entity)
    s

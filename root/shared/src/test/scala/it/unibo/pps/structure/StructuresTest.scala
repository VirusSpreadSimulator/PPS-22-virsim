package it.unibo.pps.structure

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.entity.Entities.BaseEntity
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import it.unibo.pps.entity.structure.entrance.Entrance.{BaseEntranceStrategy, FilterBasedStrategy}
import monocle.syntax.all.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class StructuresTest extends AnyFunSuite with Matchers:
  private class FilteredStrategyTrue extends BaseEntranceStrategy with FilterBasedStrategy(_.age > 18)
  private class FilteredStrategyFalse extends BaseEntranceStrategy with FilterBasedStrategy(_.age > 24)
  private val infectionProbability = 1
  private val capacity = 2
  private val position = Point2D(3, 0)
  private val treatmentQuality = Hospitalization.TreatmentQuality.GOOD
  private val timeStamp = TimeStamp(100)
  private val house = House(position, infectionProbability, capacity)
  private val building = GenericBuilding(position, infectionProbability, capacity)
  private val hospital =
    Hospital(position, infectionProbability, capacity, treatmentQuality = treatmentQuality)
  private val entities =
    Seq(
      BaseEntity(0, 23, house, position = (10L, 5L)),
      BaseEntity(1, 23, house, position = (10L, 5L)),
      BaseEntity(2, 23, house, position = (10L, 5L))
    )

  test("Initially a house is empty") {
    house.entities.size shouldBe 0
  }

  test("In a house can enter anyone") {
    var houseCopy: SimulationStructure = house.tryToEnter(entities.head, timeStamp)
    houseCopy.entities.size shouldBe 1
  }

  test("No more entities than capacity can enter in a House") {
    var houseCopy: SimulationStructure = tryToEnterMultiple(house, entities)
    houseCopy.entities.size shouldBe capacity
  }

  test("An entity can exit from an house") {
    var houseCopy: SimulationStructure = house.tryToEnter(entities.head, timeStamp)
    houseCopy = houseCopy.entityExit(entities.head)
    houseCopy.entities.isEmpty shouldBe true
  }

  test("Initially a generic building is empty") {
    building.entities.size shouldBe 0
  }

  test("In a generic building the strategy must be considered, true case") {
    var buildingCopy: SimulationStructure = building.copy(entranceStrategy = FilteredStrategyTrue())
    buildingCopy = buildingCopy.tryToEnter(entities.head, timeStamp)
    buildingCopy.entities.size shouldBe 1
  }

  test("In a generic building the strategy must be considered, false case") {
    var buildingCopy: SimulationStructure = building.copy(entranceStrategy = FilteredStrategyFalse())
    buildingCopy = buildingCopy.tryToEnter(entities.head, timeStamp)
    buildingCopy.entities.size shouldBe 0
  }

  test("A generic building can be closed after creation and will not accept entities") {
    val buildingCopy = building.focus(_.isOpen).replace(false)
    buildingCopy.tryToEnter(entities.head, timeStamp).entities.size shouldBe 0
  }

  test("In a generic building even if the strategy allow the entity, the capacity must be respected") {
    var buildingCopy: SimulationStructure = building.copy(entranceStrategy = FilteredStrategyTrue())
    buildingCopy = tryToEnterMultiple(buildingCopy, entities)
    buildingCopy.entities.size shouldBe capacity
  }

  test("An entity can exit from a generic building") {
    var buildingCopy: SimulationStructure = building.tryToEnter(entities.head, timeStamp)
    buildingCopy = buildingCopy.entityExit(entities.head)
    buildingCopy.entities.isEmpty shouldBe true
  }

  test("Initially a hospital is empty") {
    hospital.entities.size shouldBe 0
  }

  test("In a hospital the strategy must be considered, true case") {
    var hospitalCopy: SimulationStructure = hospital.copy(entranceStrategy = FilteredStrategyTrue())
    hospitalCopy = hospitalCopy.tryToEnter(entities.head, timeStamp)
    hospitalCopy.entities.size shouldBe 1
  }

  test("In a hospital the strategy must be considered, false case") {
    var hospitalCopy: SimulationStructure = hospital.copy(entranceStrategy = FilteredStrategyFalse())
    hospitalCopy = hospitalCopy.tryToEnter(entities.head, timeStamp)
    hospitalCopy.entities.size shouldBe 0
  }

  test("In a hospital even if the strategy allow the entity, the capacity must be respected") {
    var hospitalCopy: SimulationStructure = hospital.copy(entranceStrategy = FilteredStrategyTrue())
    hospitalCopy = tryToEnterMultiple(hospitalCopy, entities)
    hospitalCopy.entities.size shouldBe capacity
  }

  test("An entity can exit from a hospital") {
    var hospitalCopy: SimulationStructure = hospital.tryToEnter(entities.head, timeStamp)
    hospitalCopy = hospitalCopy.entityExit(entities.head)
    hospitalCopy.entities.isEmpty shouldBe true
  }

  private def tryToEnterMultiple(structure: SimulationStructure, entities: Seq[Entity]): SimulationStructure =
    var s = structure
    for entity <- entities do s = s.tryToEnter(entity, timeStamp)
    s

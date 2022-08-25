package it.unibo.pps.entity.structure

import it.unibo.pps.entity.Samples
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import it.unibo.pps.entity.structure.entrance.Entrance.{BaseEntranceStrategy, FilterBasedStrategy}
import it.unibo.pps.entity.TestUtils.tryToEnterMultiple
import monocle.syntax.all.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class StructuresTest extends AnyFunSuite with Matchers:
  private val filteredStrategyTrue = new BaseEntranceStrategy() with FilterBasedStrategy(_.age > 18)
  private val filteredStrategyFalse = new BaseEntranceStrategy() with FilterBasedStrategy(_.age > 24)
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
      SimulationEntity(0, 23, house.position, 80, position = (10, 5)),
      SimulationEntity(1, 23, house.position, 80, position = (10, 5)),
      SimulationEntity(2, 23, house.position, 80, position = (10, 5))
    )

  test("Initially a house is empty") {
    house.entities.isEmpty shouldBe true
  }

  test("In a house can enter anyone") {
    val houseCopy = house.tryToEnter(entities.head, timeStamp)
    houseCopy.entities.size shouldBe 1
  }

  test("No more entities than capacity can enter in a House") {
    val houseCopy = house.tryToEnterMultiple(entities, timeStamp)
    houseCopy.entities.size shouldBe capacity
  }

  test("An entity can exit from an house") {
    val houseCopy = house.tryToEnter(entities.head, timeStamp)
    houseCopy.entityExit(entities.head).entities.isEmpty shouldBe true
  }

  test("We can delete all the entities inside a house") {
    val resultingHouse = Samples.inhabitatedHouse.updateEntitiesInside(_ => None)
    resultingHouse.entities.isEmpty shouldBe true
  }

  test("We can modify all the entities inside a house and the number will remain equal") {
    val resultingHouse =
      Samples.inhabitatedHouse.updateEntitiesInside(entity => Some(entity.focus(_.age).modify(_ + 1)))
    resultingHouse.entities.size shouldBe Samples.inhabitatedHouse.entities.size
  }

  test("Initially a generic building is empty") {
    building.entities.isEmpty shouldBe true
  }

  test("In a generic building the strategy must be considered, true case") {
    val buildingCopy = building.copy(entranceStrategy = filteredStrategyTrue)
    buildingCopy.tryToEnter(entities.head, timeStamp).entities.size shouldBe 1
  }

  test("In a generic building the strategy must be considered, false case") {
    val buildingCopy = building.copy(entranceStrategy = filteredStrategyFalse)
    buildingCopy.tryToEnter(entities.head, timeStamp).entities.isEmpty shouldBe true
  }

  test("A generic building can be closed after creation and will not accept entities") {
    val buildingCopy = building.focus(_.isOpen).replace(false)
    buildingCopy.tryToEnter(entities.head, timeStamp).entities.isEmpty shouldBe true
  }

  test("In a generic building even if the strategy allow the entity, the capacity must be respected") {
    val buildingCopy = building.copy(entranceStrategy = filteredStrategyTrue)
    buildingCopy.tryToEnterMultiple(entities, timeStamp).entities.size shouldBe capacity
  }

  test("An entity can exit from a generic building") {
    val buildingCopy = building.tryToEnter(entities.head, timeStamp)
    buildingCopy.entityExit(entities.head).entities.isEmpty shouldBe true
  }

  test("We can delete all the entities inside a generic building") {
    val resultingHouse = Samples.inhabitatedGenericBuilding.updateEntitiesInside(_ => None)
    resultingHouse.entities.isEmpty shouldBe true
  }

  test("We can modify all the entities inside a generic building and the number will remain equal") {
    val resultingHouse =
      Samples.inhabitatedGenericBuilding.updateEntitiesInside(entity => Some(entity.focus(_.age).modify(_ + 1)))
    resultingHouse.entities.size shouldBe Samples.inhabitatedGenericBuilding.entities.size
  }

  test("Initially a hospital is empty") {
    hospital.entities.isEmpty shouldBe true
  }

  test("In a hospital the strategy must be considered, true case") {
    val hospitalCopy = hospital.copy(entranceStrategy = filteredStrategyTrue)
    hospitalCopy.tryToEnter(entities.head, timeStamp).entities.size shouldBe 1
  }

  test("In a hospital the strategy must be considered, false case") {
    val hospitalCopy = hospital.copy(entranceStrategy = filteredStrategyFalse)
    hospitalCopy.tryToEnter(entities.head, timeStamp).entities.isEmpty shouldBe true
  }

  test("In a hospital even if the strategy allow the entity, the capacity must be respected") {
    val hospitalCopy = hospital.copy(entranceStrategy = filteredStrategyTrue)
    hospitalCopy.tryToEnterMultiple(entities, timeStamp).entities.size shouldBe capacity
  }

  test("An entity can exit from a hospital") {
    val hospitalCopy = hospital.tryToEnter(entities.head, timeStamp)
    hospitalCopy.entityExit(entities.head).entities.isEmpty shouldBe true
  }

  test("We can delete all the entities inside a hospital") {
    val resultingHouse = Samples.inhabitatedHospital.updateEntitiesInside(_ => None)
    resultingHouse.entities.isEmpty shouldBe true
  }

  test("We can modify all the entities inside a hospital and the number will remain equal") {
    val resultingHouse =
      Samples.inhabitatedHospital.updateEntitiesInside(entity => Some(entity.focus(_.age).modify(_ + 1)))
    resultingHouse.entities.size shouldBe Samples.inhabitatedHospital.entities.size
  }

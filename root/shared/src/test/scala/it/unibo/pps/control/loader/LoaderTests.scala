package it.unibo.pps.control.loader

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.BoundaryModule.ConfigBoundary
import it.unibo.pps.boundary.component.Events
import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.engine.config.EngineConfiguration.given_Scheduler
import it.unibo.pps.control.loader.LoaderSamples.TestLoader
import it.unibo.pps.control.loader.configuration.ConfigurationComponent
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration, given}
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.simulation
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.structures
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.virus
import it.unibo.pps.control.parser.ParserModule
import it.unibo.pps.control.parser.ReaderModule.{FilePath, StringFilePath}
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.entity.EntityFactory
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.structure.Structures.{House, SimulationStructure}
import it.unibo.pps.entity.Samples
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{MAX_VALUES, MIN_VALUES}
import it.unibo.pps.entity.structure.StructureComponent.Habitable
import it.unibo.pps.entity.common.Utils.select
import monix.eval.Task
import monix.reactive.Observable
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import weaver.monixcompat.*

import java.nio.file.Path
import scala.concurrent.Promise

object LoaderTests extends SimpleTaskSuite with Matchers:

  private val loader: LoaderModule.Loader = TestLoader.loader
  private val env: EnvironmentModule.Environment = Samples.sampleEnv

  test("Loader should be able to create the set of entities from the configuration") {
    val entityFactory: EntityFactory = EntityFactory()
    val entities: Int = 200
    val house: SimulationStructure = House(Point2D(10, 10), 0.8, entities)
    val simulation = Simulation(numberOfEntities = entities)
    for result <- entityFactory.create(VirsimConfiguration(simulation, virus, structures), Seq(house))
    yield expect(result.size == entities)
  }

  test("Loader should create a correct number of entities") {
    for
      environment <- loader.createEnvironment(VirsimConfiguration(simulation, virus, structures))
      cardinality = environment.allEntities.size
    yield expect(
      cardinality >= MIN_VALUES.MIN_NUMBER_OF_ENTITIES && cardinality <= MAX_VALUES.MAX_NUMBER_OF_ENTITIES
    )
  }

  test("Loader should create a correct environment grid") {
    for
      environment <- loader.createEnvironment(VirsimConfiguration(simulation, virus, structures))
      side = environment.gridSide
    yield expect(side >= MIN_VALUES.MIN_GRID_SIZE && side <= MAX_VALUES.MAX_GRID_SIZE)
  }

  test("Loader should create number of houses equals to the grid side") {
    for
      environment <- loader.createEnvironment(VirsimConfiguration(simulation, virus, structures))
      houses = environment.structures.select[SimulationStructure with Habitable]
    yield expect(houses.size == environment.gridSide)
  }

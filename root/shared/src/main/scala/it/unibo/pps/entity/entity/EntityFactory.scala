package it.unibo.pps.entity.entity

import it.unibo.pps.control.loader.configuration.ConfigurationComponent.Configuration
import it.unibo.pps.control.loader.configuration.SimulationDefaults.StructuresDefault
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.structure.Structures.House
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import monix.eval.Task

import scala.util.Random

trait EntityFactory:

  /** @param configuration
    *   the parameters of simulation.
    * @return
    *   the Set of simulation entities.
    */
  def create(configuration: Configuration): Task[Set[SimulationEntity]]

object EntityFactory:
  def apply(): EntityFactory = new EntityFactoryImpl()

  private class EntityFactoryImpl() extends EntityFactory:

    override def create(configuration: Configuration): Task[Set[SimulationEntity]] =
      val houses =
        for i <- 0 until (configuration.simulation.numberOfEntities / configuration.simulation.peoplePerHouse)
        yield House(
          (Random.nextInt(configuration.simulation.gridSide + 1), configuration.simulation.gridSide),
          StructuresDefault.HOUSE_INFECTION_PROB,
          configuration.simulation.peoplePerHouse
        )

      val entities =
        for
          i <- 0 until configuration.simulation.numberOfEntities
          entityId = i
          age = GaussianIntDistribution(
            configuration.simulation.averagePopulationAge,
            configuration.simulation.stdDevPopulationAge
          ).next()
          house = houses(i % houses.size)
          position = Point2D(
            Random.nextInt(configuration.simulation.gridSide),
            Random.nextInt(configuration.simulation.gridSide)
          )
          infected =
            if i % configuration.simulation.peoplePerHouse == 0 then
              Some(
                Infection(
                  configuration.virusConfiguration.severeDeseaseProbability,
                  GaussianIntDistribution(
                    configuration.virusConfiguration.averagePositivityDays,
                    configuration.virusConfiguration.stdDevPositivityDays
                  ).next()
                )
              )
            else None
          entity = BaseEntity(entityId, age, house, position = position, infection = infected)
        yield entity
      Task(entities.toSet)

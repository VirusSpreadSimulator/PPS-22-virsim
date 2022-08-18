package it.unibo.pps.entity.entity

import it.unibo.pps.control.loader.configuration.ConfigurationComponent.Configuration
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{MAX_VALUES, StructuresDefault}
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.Structures.House
import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import it.unibo.pps.entity.common.ProblableEvents.*
import it.unibo.pps.entity.common.ProblableEvents.ProbabilityResult.*
import it.unibo.pps.entity.common.ProblableEvents.ProbableOps.*
import it.unibo.pps.entity.common.ProblableEvents.ProbableGivenInstance.given
import it.unibo.pps.entity.entity.Infection.*
import it.unibo.pps.entity.common.Time.TimeStamp

import scala.concurrent.duration.DAYS
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
        for _ <- 0 until (configuration.simulation.numberOfEntities / configuration.simulation.peoplePerHouse)
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
            if i % configuration.simulation.startingInfectedPercentage == 0 then
              Some(
                Infection(
                  configuration.virusConfiguration.severeDeseaseProbability.isHappening match
                    case HAPPENED => Severity.SERIOUS()
                    case NOTHAPPENED => Severity.LIGHT()
                  ,
                  TimeStamp(),
                  GaussianDurationTime(
                    configuration.virusConfiguration.averagePositivityDays,
                    configuration.virusConfiguration.stdDevPositivityDays,
                    DAYS
                  ).next()
                )
              )
            else None
          entity = SimulationEntity(
            entityId,
            age,
            house,
            MAX_VALUES.MAX_HEALTH,
            position = position,
            infection = infected
          )
        yield entity
      Task(entities.toSet)

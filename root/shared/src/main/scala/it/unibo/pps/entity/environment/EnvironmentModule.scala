package it.unibo.pps.entity.environment

import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL
import it.unibo.pps.entity.common.Time.{DurationTime, TimeStamp}
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import monocle.syntax.all.*
import scala.concurrent.duration.DAYS

/** The environment of the simulation. */
object EnvironmentModule:

  trait Environment:
    def time: TimeStamp
    def gridSide: Int
    def externalEntities: Set[SimulationEntity]
    def structures: Set[SimulationStructure]
    def allEntities: Set[SimulationEntity]
    def virus: Virus
    def environmentDuration: DurationTime
    def deadEntities: Set[SimulationEntity]

    def update(
        time: TimeStamp = time,
        gridSide: Int = gridSide,
        externalEntities: Set[SimulationEntity] = externalEntities,
        structures: Set[SimulationStructure] = structures,
        virus: Virus = virus,
        environmentDuration: DurationTime = environmentDuration,
        deadEntities: Set[SimulationEntity] = deadEntities
    ): Environment

  trait Provider:
    val env: Environment
  trait Component:

    object Environment:
      def empty: Environment = EnvironmentImpl()

    case class EnvironmentImpl(
        override val time: TimeStamp = TimeStamp(),
        override val gridSide: Int = GlobalDefaults.GRID_SIDE,
        override val externalEntities: Set[SimulationEntity] = Set(),
        override val structures: Set[SimulationStructure] = Set(),
        override val virus: Virus = Virus(),
        override val environmentDuration: DurationTime = DurationTime(GlobalDefaults.DURATION, DAYS),
        override val deadEntities: Set[SimulationEntity] = Set()
    ) extends Environment:

      override def update(
          time: TimeStamp = time,
          gridSide: Int = gridSide,
          externalEntities: Set[SimulationEntity] = externalEntities,
          structures: Set[SimulationStructure] = structures,
          virus: Virus = virus,
          environmentDuration: DurationTime = environmentDuration,
          deadEntities: Set[SimulationEntity] = deadEntities
      ): Environment =
        EnvironmentImpl(time, gridSide, externalEntities, structures, virus, environmentDuration, deadEntities)

      override def allEntities: Set[SimulationEntity] =
        import it.unibo.pps.entity.common.Utils.*
        externalEntities ++ structures.flatMap(_.entities).map(_.entity)

  trait Interface extends Provider with Component

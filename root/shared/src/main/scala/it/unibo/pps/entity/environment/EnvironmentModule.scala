package it.unibo.pps.entity.environment

import it.unibo.pps.entity.State
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL
import it.unibo.pps.entity.common.Time.TimeStamp
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import monocle.syntax.all.*

object EnvironmentModule:

  trait Environment:
    def time: TimeStamp
    def gridSide: Int
    def entities: Set[SimulationEntity]
    def structures: Set[SimulationStructure]
    def virus: Virus
    def status: EnvironmentStatus

    // todo: to be deleted or rethink
    def initialized(
        gridSide: Int,
        entities: Set[SimulationEntity],
        virus: Virus,
        structures: Set[SimulationStructure]
    ): Environment

    def update(
        time: TimeStamp = time,
        gridSide: Int = gridSide,
        entities: Set[SimulationEntity] = entities,
        structures: Set[SimulationStructure] = structures,
        virus: Virus = virus,
        status: EnvironmentStatus = status
    ): Environment

  trait Provider:
    val env: Environment
  trait Component:

    object Environment:
      def empty: Environment = EnvironmentImpl()

    case class EnvironmentImpl(
        override val time: TimeStamp = TimeStamp(),
        override val gridSide: Int = GlobalDefaults.GRID_SIDE,
        override val entities: Set[SimulationEntity] = Set(),
        override val structures: Set[SimulationStructure] = Set(),
        override val virus: Virus = Virus(),
        override val status: EnvironmentStatus = EnvironmentStatus.EVOLVING
    ) extends Environment:

      // todo: to be deleted or rethink
      override def initialized(
          gridSize: Int,
          simulationEntities: Set[SimulationEntity],
          simulationVirus: Virus,
          simulationStructures: Set[SimulationStructure]
      ): Environment =
        EnvironmentImpl(
          gridSide = gridSize,
          entities = simulationEntities,
          structures = simulationStructures,
          virus = simulationVirus
        )

      override def update(
          time: TimeStamp = time,
          gridSide: Int = gridSide,
          entities: Set[SimulationEntity] = entities,
          structures: Set[SimulationStructure] = structures,
          virus: Virus = virus,
          status: EnvironmentStatus = status
      ): Environment =
        EnvironmentImpl(time, gridSide, entities, structures, virus, status)

  trait Interface extends Provider with Component

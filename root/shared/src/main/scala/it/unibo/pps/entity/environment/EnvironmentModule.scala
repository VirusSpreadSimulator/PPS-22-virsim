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

/** The environment of the simulation */
object EnvironmentModule:

  trait Environment:
    /** The time of the simulation.
      * @return
      *   the current time.
      */
    def time: TimeStamp

    /** The size of the environment grid.
      * @return
      *   the grid size.
      */
    def gridSide: Int

    /** The set of entities inside the environment but external to structures.
      * @return
      *   the set of external entities.
      */
    def externalEntities: Set[SimulationEntity]

    /** The set of structures inside the environment.
      *
      * @return
      *   the set of structures.
      */
    def structures: Set[SimulationStructure]

    /** The Set of all entities inside the environment and inside structures.
      * @return
      *   the set of entities.
      */
    def allEntities: Set[SimulationEntity]

    /** The virus of the simulation that infects entities.
      * @return
      *   the virus.
      */
    def virus: Virus

    /** The duration of the simulation.
      * @return
      *   the duration.
      */
    def environmentDuration: DurationTime

    /** The number of dead entities in the environment.
      * @return
      *   the dead entities.
      */
    def deadEntities: Set[SimulationEntity]

    /** @param time
      *   the time of the simulation.
      * @param gridSide
      *   the size of the grid.
      * @param externalEntities
      *   the set of external entities.
      * @param structures
      *   the set of structures inside the environment.
      * @param virus
      *   the virus inside the environment.
      * @param environmentDuration
      *   the duration of the simulation.
      * @param deadEntities
      *   the number of dead entities.
      * @return
      */
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

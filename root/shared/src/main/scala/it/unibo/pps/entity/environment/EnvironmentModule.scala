package it.unibo.pps.entity.environment

import it.unibo.pps.entity.State
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL
import it.unibo.pps.entity.common.Time.TimeStamp
import monocle.syntax.all.*

object EnvironmentModule:

  trait Environment:
    def time: TimeStamp
    def gridSide: Int
    def entities: Set[Entity]
    def structures: Set[Structure]
    def virus: Virus

    def initializeEnvironment(entities: Set[Entity], virus: Virus, structures: Set[Structure]): Unit

  trait Provider:
    val env: Environment
  trait Component:
    case class EnvironmentImpl(
        override val time: TimeStamp = TimeStamp(0),
        override val gridSide: Int = 50,
        override val entities: Set[Entity] = Set(),
        override val structures: Set[Structure] = Set(),
        override val virus: Virus = Virus()
    ) extends Environment:

      override def initializeEnvironment(
          simulationEntities: Set[Entity],
          simulationVirus: Virus,
          simulationStructures: Set[Structure]
      ): Unit =
        this.focus(_.entities).replace(simulationEntities)
        this.focus(_.virus).replace(simulationVirus)
        this.focus(_.structures).replace(simulationStructures)

  trait Interface extends Provider with Component

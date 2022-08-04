package it.unibo.pps.entity.Environment

import it.unibo.pps.entity.entity.Entities
import it.unibo.pps.entity.entity.EntityComponent.Entity
import it.unibo.pps.entity.structure.StructureComponent.Structure
import it.unibo.pps.entity.virus.VirusComponent.Virus

/** The environment represent the container for the properties of the simulation.
  * @param width
  *   the width of the environment.
  * @param height
  *   the height of the environment.
  * @param simulationDays
  *   the number of day of the simulation.
  * @param entities
  *   set that contains all the entity of the simulation.
  * @param structure
  *   set that contains all the entity of the simulation.
  * @param virus
  *   the virus of the simulation.
  */
case class Environment(
    width: Int,
    height: Int,
    simulationDays: Long,
    entities: Set[Entity],
    structure: Set[Structure],
    virus: Virus
)

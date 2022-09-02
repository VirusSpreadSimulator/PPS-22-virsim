package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.SimulationStructure

/** The DSL of the simulation structures for the configuration file. */
object StructuresDSL:

  /** The Set of structures defined in the configuration. */
  val structures: Set[SimulationStructure] = Set()

  extension [A](set: Set[A])
    def are(variable: A): Set[A] = set + variable
    def and(variable: A): Set[A] = set + variable

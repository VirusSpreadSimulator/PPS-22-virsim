package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.SimulationStructure

object StructuresDSL:

  val structures: Set[SimulationStructure] = Set()

  extension [A](set: Set[A])
    def are(variable: A): Set[A] = set + variable
    def and(variable: A): Set[A] = set + variable

  class Point(x: Int, y: Int) extends Point2D(x * GlobalDefaults.GRID_MULTIPLIER, y * GlobalDefaults.GRID_MULTIPLIER)

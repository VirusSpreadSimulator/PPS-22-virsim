package it.unibo.pps.control.loader.configuration.dsl

import it.unibo.pps.entity.structure.StructureComponent.Structure

object StructuresDSL:

  val structures: Set[Structure] = Set()

  extension [A](set: Set[A])
    def are(variable: A): Set[A] = set + variable
    def and(variable: A): Set[A] = set + variable

package it.unibo.pps.entity.structure.entrance

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.common.Space.Distance
import it.unibo.pps.entity.common.Time.DurationTime

object StructureComponent:
  trait Structure:
    type Probability
    type Position
    type Entity
    type EntranceResult

    def position: Position
    def infectionProbability: Probability
    def capacity: Int
    def visibilityDistance: Distance
    def permanenceTime: DurationTime
    def isOpen: Boolean
    def isOpen_=(state: Boolean): Unit
    def tryToEnter(entity: Entity): EntranceResult
//todo: complete the interface and include some methods already implemented with the abstract types (if there are)

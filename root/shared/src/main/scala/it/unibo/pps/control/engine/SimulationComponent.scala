package it.unibo.pps.control.engine

object SimulationComponent:

  class Simulation:
    var duration: Option[Int] = None
    var numberOfEntities: Option[Int] = None
    var peoplePerHouse: Option[Int] = Some(4)
    var averagePopulationAge: Option[Int] = Some(40)
    var stdDevPopulationAge: Option[Double] = Some(0.5)
    var startingInfectedPercentage: Option[Double] = Some(10)

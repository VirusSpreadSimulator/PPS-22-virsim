package it.unibo.pps.jvm.boundary.exporter

import Extractors.DataExtractor
import it.unibo.pps.jvm.boundary.exporter.Extractors.{
  Alive,
  DataExtractor,
  Days,
  Deaths,
  HospitalFreeSeats,
  HospitalPressure,
  Hospitalized,
  HospitalsCapacity,
  Hours,
  Infected,
  Sick
}
import it.unibo.pps.jvm.boundary.exporter.Extractors.DataExtractor

/** All the statistics of the simulation. These data are used both from in the export module and by charts to monitor
  * the simulation.
  */
object StatisticalData:

  enum Stats:
    // The current days of the simulation.
    case DAYS
    // The current hour of the day
    case HOURS
    // The pressure of sick people inside hospitals.
    case HOSPITAL_PRESSURE
    // The sum of all hospitals capacity.
    case TOTAL_HOSPITALS_CAPACITY
    // The sum of remaining free seats of all hospitals.
    case HOSPITAL_FREE_SEATS
    // The current entities inside hospitals.
    case HOSPITALIZED
    // The number of current alive entities in the environment.
    case ALIVE
    // The number of deaths in the environment.
    case DEATHS
    // The current number of sick people.
    case SICK
    // The current number of infected entities inside the environment.
    case INFECTED

  // Takes in input a Statistic and returns its Extractor
  given Conversion[Stats, DataExtractor[_]] with
    override def apply(stat: Stats): DataExtractor[_] =
      stat match
        case Stats.DAYS => Days()
        case Stats.HOURS => Hours()
        case Stats.HOSPITAL_PRESSURE => HospitalPressure()
        case Stats.TOTAL_HOSPITALS_CAPACITY => HospitalsCapacity()
        case Stats.HOSPITAL_FREE_SEATS => HospitalFreeSeats()
        case Stats.HOSPITALIZED => Hospitalized()
        case Stats.ALIVE => Alive()
        case Stats.DEATHS => Deaths()
        case Stats.SICK => Sick()
        case Stats.INFECTED => Infected()

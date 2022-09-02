package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.select

/** All the extractors of statistics about hospital. */
object HospitalStats:

  /** The implementation of the Hospital Capacity statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class HospitalsCapacity(override val name: String = "HospitalsCapacity") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .select[SimulationStructure with Hospitalization]
        .foldLeft(0)((capacity, hospital) => capacity + hospital.capacity)

  /** The implementation of the Hospital Free Seats statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class HospitalFreeSeats(override val name: String = "FreeSeats") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .select[SimulationStructure with Hospitalization]
        .foldLeft(0)((freeSeats, hospital) => freeSeats + (hospital.capacity - hospital.entities.size))

  /** The implementation of the Hospitalized statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class Hospitalized(override val name: String = "Hospitalized") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .select[SimulationStructure with Hospitalization]
        .foldLeft(0)((hospitalized, hospital) => hospitalized + hospital.entities.size)

  /** The implementation of the Hospital pressure statistic.
    * @param name
    *   the name of this data extractor.
    */
  case class HospitalPressure(override val name: String = "HospitalPressure") extends DataExtractor[Double]:
    override def extractData(env: Environment): Double =
      if env.structures.select[SimulationStructure with Hospitalization].nonEmpty &&
        Hospitalized().extractData(env) > 0
      then
        BigDecimal((Hospitalized().extractData(env).toDouble / HospitalsCapacity().extractData(env)) * 100)
          .setScale(2, BigDecimal.RoundingMode.HALF_UP)
          .toDouble
      else 0

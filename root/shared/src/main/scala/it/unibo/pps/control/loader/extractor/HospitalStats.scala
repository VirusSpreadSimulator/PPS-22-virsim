package it.unibo.pps.control.loader.extractor

import it.unibo.pps.control.loader.extractor.Extractor.DataExtractor
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Hospitalization
import it.unibo.pps.entity.structure.Structures.SimulationStructure
import it.unibo.pps.entity.common.Utils.select

object HospitalStats:

  case class HospitalsCapacity(override val name: String = "HospitalsCapacity") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .select[SimulationStructure with Hospitalization]
        .foldLeft(0)((capacity, hospital) => capacity + hospital.capacity)

  case class HospitalFreeSeats(override val name: String = "FreeSeats") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .select[SimulationStructure with Hospitalization]
        .foldLeft(0)((freeSeats, hospital) => freeSeats + (hospital.capacity - hospital.entities.size))

  case class Hospitalized(override val name: String = "Hospitalized") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .select[SimulationStructure with Hospitalization]
        .foldLeft(0)((hospitalized, hospital) => hospitalized + hospital.entities.size)

  case class HospitalPressure(override val name: String = "HospitalPressure") extends DataExtractor[Double]:
    override def extractData(env: Environment): Double =
      Math.ceil(Hospitalized().extractData(env).toDouble / HospitalsCapacity().extractData(env).toDouble)
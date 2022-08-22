package it.unibo.pps.jvm.boundary.exporter

import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.Structures.Hospital
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults

object Extractors:

  /** The extractor is responsible to extract some statistics from the environment.
    * @tparam E
    *   The type of the extracted data.
    */
  trait DataExtractor[E]:
    def name: String
    def extractData(env: Environment): E

  case class Days(override val name: String = "Days") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.iteration

  case class Hours(override val name: String = "Hour") extends DataExtractor[Long]:
    override def extractData(env: Environment): Long = env.time.toHours

  case class HospitalPressure(override val name: String = "HospitalPressure") extends DataExtractor[Double]:
    override def extractData(env: Environment): Double =
      env.structures
        .filter(struct => struct.isInstanceOf[Hospital] && struct.entities.nonEmpty)
        .foldLeft(0)((pressure, hospital) => pressure + (hospital.capacity / hospital.entities.size))

  case class HospitalsCapacity(override val name: String = "HospitalsCapacity") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .filter(struct => struct.isInstanceOf[Hospital])
        .foldLeft(0)((capacity, hospital) => capacity + hospital.capacity)

  case class HospitalFreeSeats(override val name: String = "HospitalsFreeSeats") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .filter(struct => struct.isInstanceOf[Hospital])
        .foldLeft(0)((freeSeats, hospital) => freeSeats + (hospital.capacity - hospital.entities.size))

  case class Hospitalized(override val name: String = "Hospitalized") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.structures
        .filter(struct => struct.isInstanceOf[Hospital])
        .foldLeft(0)((hospitalized, hospital) => hospitalized + hospital.entities.size)

  case class Alive(override val name: String = "Alive") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int = env.allEntities.size

  case class Deaths(override val name: String = "Deaths") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int = env.deadEntities.size

  case class Sick(override val name: String = "Sick") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(entity => entity.health < GlobalDefaults.MIN_HEALTH_TO_GET_SICK)

  case class Infected(override val name: String = "Infected") extends DataExtractor[Int]:
    override def extractData(env: Environment): Int =
      env.allEntities.count(e => e.infection.isDefined)

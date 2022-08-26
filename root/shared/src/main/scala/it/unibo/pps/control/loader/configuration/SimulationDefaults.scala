package it.unibo.pps.control.loader.configuration

import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime
import scala.concurrent.duration.{HOURS, MINUTES}

object SimulationDefaults:

  object GlobalDefaults:
    val GRID_SIDE: Int = 80
    val DURATION: Int = 7
    val NUMBER_OF_ENTITIES: Int = 100
    val AVERAGE_POPULATION_AGE: Int = 40
    val STD_DEV_POPULATION_AGE: Double = 10
    val STARTING_INFECTED_PERCENTAGE: Double = 5.0
    val DSL_IMPORTS: String =
      "import it.unibo.pps.control.loader.configuration.ConfigurationComponent.VirsimConfiguration\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*\n" +
        "import it.unibo.pps.entity.structure.Structures.*\n" +
        "import it.unibo.pps.entity.common.GaussianProperty.GaussianDurationTime\n" +
        "import scala.concurrent.duration.*\n" +
        "import it.unibo.pps.entity.structure.entrance.Entrance.*\n" +
        "import it.unibo.pps.entity.structure.StructureComponent.Hospitalization.TreatmentQuality.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*\n\n"
    val ENTITY_PROBABILITY_TO_RETURN_HOME: Double = 0.35
    val MIN_HEALTH_TO_GET_SICK: Double = 50.0
    val EXPORT_DIR_NAME: String = "virsim-export"
    val EXPORT_FILE_NAME: String = "simulation_stats.csv"
    val EXPORT_INTERVAL: Double = 60

  object VirusDefaults:
    val NAME: String = "Default-Virus"
    val SPREAD_RATE: Double = 0.05
    val AVERAGE_POSITIVITY_DAYS: Int = 2
    val STD_DEV_POSITIVITY_DAYS: Double = 1.0
    val SEVERE_DESEASE_PROBABILITY: Double = 0.15
    val MAXIMUM_INFECTION_DISTANCE: Double = 2
    val HEALTH_GAIN: Double = 0.01
    val HEALTH_INFECTED_LOSS: Double = 0.02
    val IMMUNITY_LOSS: Double = 0.001
    val IMMUNITY_GAIN_RECOVERY: Double = 30
    val IMMUNITY_GAIN_VACCINATION: Double = 30
    val MASK_REDUCER: Int = 2

  object StructuresDefault:
    val HOUSE_INFECTION_PROB: Double = 0.02
    val HOSPITAL_HEALTH_GAIN: Double = 0.08
    val DEFAULT_VISIBILITY_DISTANCE: Double = 2
    val DEFAULT_HOUSE_VISIBILITY_DISTANCE: Double = 0
    val DEFAULT_GROUP: String = "base"
    val DEFAULT_PERMANENCE_TIME_DISTRIBUTION: GaussianDurationTime = GaussianDurationTime(20, 5, MINUTES)
    val DEFAULT_HOUSE_PERMANENCE_TIME_DISTRIBUTION: GaussianDurationTime = GaussianDurationTime(5, 1, HOURS)

  object MAX_VALUES:
    val MAX_GRID_SIZE: Int = 100
    val MAX_NUMBER_OF_ENTITIES: Int = 1000
    val MAX_HEALTH: Int = 100
    val MAX_IMMUNITY: Double = 100

  object MIN_VALUES:
    val MIN_GRID_SIZE: Int = 20
    val MIN_NUMBER_OF_ENTITIES = 2
    val MIN_HEALTH: Int = 0
    val MIN_INITIAL_HEALTH: Int = 70
    val MIN_IMMUNITY: Int = 0
    val HOSPITALIZATION_HEALTH_LIMIT = 15

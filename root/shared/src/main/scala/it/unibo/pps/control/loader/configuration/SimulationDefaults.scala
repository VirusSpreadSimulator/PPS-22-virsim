package it.unibo.pps.control.loader.configuration

object SimulationDefaults:

  object GlobalDefaults:
    val GRID_SIDE: Int = 25
    val GRID_MULTIPLIER: Int = 4
    val DURATION: Int = 30
    val NUMBER_OF_ENTITIES: Int = 100
    val AVERAGE_POPULATION_AGE: Int = 40
    val STD_DEV_POPULATION_AGE: Double = 10
    val STARTING_INFECTED_PERCENTAGE: Double = 10.0
    val DSL_IMPORTS: String =
      "import it.unibo.pps.control.loader.configuration.ConfigurationComponent.VirsimConfiguration\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*\n" +
        "import it.unibo.pps.entity.structure.Structures.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.given\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*\n\n"
    val ENTITY_PROBABILITY_TO_RETURN_HOME: Double = 0.35
    val MIN_HEALTH_TO_GET_SICK: Double = 20.0
    val EXPORT_DIR_NAME: String = "virsim-export"
    val EXPORT_FILE_NAME: String = "simulation_stats.csv"

    val EXPORT_INTERVAL: Double = 60

  object VirusDefaults:
    val NAME: String = "Default-Virus"
    val SPREAD_RATE: Double = 0.5
    val AVERAGE_POSITIVITY_DAYS: Int = 7
    val STD_DEV_POSITIVITY_DAYS: Double = 3
    val SEVERE_DESEASE_PROBABILITY: Double = 0.75
    val MAXIMUM_INFECTION_DISTANCE: Double = 0.5
    val HEALTH_GAIN: Double = 0.01
    val HEALTH_INFECTED_LOSS: Double = 0.01
    val IMMUNITY_LOSS: Double = 0.001
    val IMMUNITY_GAIN_RECOVERY: Double = 30
    val IMMUNITY_GAIN_VACCINATION: Double = 30
    val MASK_REDUCER: Int = 2

  object StructuresDefault:
    val HOUSE_INFECTION_PROB: Double = 0.5
    val HOSPITAL_HEALTH_GAIN: Double = 0.08

  object MAX_VALUES:
    val MAX_GRID_SIZE: Int = 100
    val MAX_NUMBER_OF_ENTITIES: Int = 1000
    val MAX_HEALTH: Int = 100
    val MAX_IMMUNITY: Double = 100

  object MIN_VALUES:
    val MIN_GRID_SIZE: Int = 5
    val MIN_NUMBER_OF_ENTITIES = 2
    val MIN_HEALTH: Int = 0
    val MIN_IMMUNITY: Int = 0
    val HOSPITALIZATION_HEALTH_LIMIT = 15

package it.unibo.pps.control.loader.configuration

object SimulationDefaults:

  object GlobalDefaults:
    val GRID_SIDE: Int = 25
    val GRID_MULTIPLIER: Int = 4
    val DURATION: Int = 30
    val NUMBER_OF_ENTITIES: Int = 100
    val PEOPLE_PER_HOUSE: Int = 4
    val AVERAGE_POPULATION_AGE: Int = 40
    val STD_DEV_POPULATION_AGE: Double = 10
    val STARTING_INFECTED_PERCENTAGE: Double = 10.0
    val DSL_IMPORTS: String =
      "import it.unibo.pps.control.loader.configuration.ConfigurationComponent.VirsimConfiguration\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*\n" +
        "import it.unibo.pps.entity.structure.Structures.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*\n\n"

  object VirusDefaults:
    val NAME: String = "Default-Virus"
    val SPREAD_RATE: Double = 0.5
    val AVERAGE_POSITIVITY_DAYS: Int = 7
    val STD_DEV_POSITIVITY_DAYS: Double = 3
    val SEVERE_DESEASE_PROBABILITY: Double = 0.75
    val MAXIMUM_INFECTION_DISTANCE: Double = 0.5

  object StructuresDefault:
    val HOUSE_INFECTION_PROB: Double = 0.5

  object MAX_VALUES:
    val MAX_GRID_SIZE: Int = 100
    val MAX_NUMBER_OF_ENTITIES: Int = 1000

  object MIN_VALUES:
    val MIN_GRID_SIZE: Int = 5
    val MIN_NUMBER_OF_ENTITIES = 2

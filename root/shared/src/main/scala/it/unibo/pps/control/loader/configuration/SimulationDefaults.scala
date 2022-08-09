package it.unibo.pps.control.loader.configuration

object SimulationDefaults:

  object GlobalDefaults:
    val GRID_SIDE: Int = 25
    val DURATION: Int = 30
    val NUMBER_OF_ENTITIES: Int = 100
    val PEOPLE_PER_HOUSE: Int = 4
    val AVERAGE_POPULATION_AGE: Int = 40
    val STD_DEV_POPULATION_AGE: Double = 0.8
    val STARTING_INFECTED_PERCENTAGE: Double = 10.0
    val DSL_IMPORTS: String =
      "import it.unibo.pps.control.loader.configuration.ConfigurationComponent.VirsimConfiguration\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*\n" +
        "import it.unibo.pps.entity.structure.Structures.*\n" +
        "import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*\n\n"

  object VirusDefaults:
    val NAME: String = "Default-Virus"
    val SPREAD_RATE: Double = 1.5
    val AVERAGE_POSITIVITY_DAYS: Int = 7
    val SEVERE_DESEASE_PROBABILITY: Int = 25

  object MAX_VALUES:
    val MAX_GRID_SIZE: Int = 50

  object MIN_VALUES:
    val MIN_GRID_SIZE: Int = 5
    val MIN_NUMBER_OF_ENTITIES = 2

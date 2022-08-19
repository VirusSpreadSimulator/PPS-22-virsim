VirsimConfiguration(

  simulation gridSide 25 days 7 entities 100 averagePopulationAge 40 stdDevPopulationAge 0.5 startingInfectedPercentage 10,

  virus spreadRate 0.8 averagePositivityDays 7 stdDevPositivityDays 3.0 severeDeseaseProbability 0.75,

  structures are GenericBuilding(position = (10, 10), infectionProbability = 20, capacity = 30) and
                 GenericBuilding(position = (20, 20), infectionProbability = 25, capacity = 40)
)

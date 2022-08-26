VirsimConfiguration(

simulation gridSide 80 days 7 entities 100 averagePopulationAge 40 stdDevPopulationAge 10 startingInfectedPercentage 5,

virus spreadRate 0.05 averagePositivityDays 2 stdDevPositivityDays 1.0 severeDeseaseProbability 0.15 maxInfectionDistance 2,

structures are  GenericBuilding(position = (5, 5),
                                infectionProbability = 0.2,
                                capacity = 10,
                                group = "shop") and
                GenericBuilding(position = (60, 10),
                                infectionProbability = 0.4,
                                capacity = 15,
                                permanenceTimeDistribution = GaussianDurationTime(2, 0.5, HOURS),
                                group = "disco",
                                entranceStrategy = new BaseEntranceStrategy() with FilterBasedStrategy(_.age > 18)) and
                GenericBuilding(position = (20, 70),
                                infectionProbability = 0.2,
                                capacity = 20,
                                group = "market")  and
                GenericBuilding(position = (70, 70),
                                infectionProbability = 0.8,
                                capacity = 15,
                                visibilityDistance = 1,
                                group = "shop") and
                GenericBuilding(position = (73, 70),
                                infectionProbability = 0.8,
                                capacity = 15,
                                visibilityDistance = 1,
                                group = "office") and
                GenericBuilding(position = (76, 70),
                                infectionProbability = 0.8,
                                capacity = 15,
                                visibilityDistance = 1,
                                group = "office") and
                GenericBuilding(position = (67, 70),
                                infectionProbability = 0.8,
                                capacity = 15,
                                visibilityDistance = 1,
                                group = "shop") and
                GenericBuilding(position = (64, 70),
                                infectionProbability = 0.8,
                                capacity = 15,
                                visibilityDistance = 1,
                                group = "shop") and
                GenericBuilding(position = (10, 5),
                                infectionProbability = 0.7,
                                capacity = 5,
                                permanenceTimeDistribution = GaussianDurationTime(1, 0.5, DAYS)) and
                GenericBuilding(position = (5, 10),
                                infectionProbability = 0.6,
                                capacity = 5, group = "market") and
                GenericBuilding(position = (15, 35),
                                infectionProbability = 0.9,
                                capacity = 10,
                                visibilityDistance = 3,
                                group = "disco",
                                entranceStrategy = new BaseEntranceStrategy() with FilterBasedStrategy(_.age < 50) with ProbabilityBasedStrategy(0.5)) and
                GenericBuilding(position = (75, 45),
                                infectionProbability = 0.3,
                                capacity = 5) and
                GenericBuilding(position = (75, 40),
                                infectionProbability = 0.3,
                                capacity = 5) and
                GenericBuilding(position = (75, 50),
                                infectionProbability = 0.3,
                                capacity = 5) and
                Hospital(position = (40, 40),
                                infectionProbability = 0.02,
                                capacity = 20,
                                treatmentQuality = GOOD)
)

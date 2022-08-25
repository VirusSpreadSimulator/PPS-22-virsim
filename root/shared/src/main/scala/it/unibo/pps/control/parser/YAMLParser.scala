package it.unibo.pps.control.parser

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, SimulationStructure}
import it.unibo.pps.entity.virus.VirusComponent.Virus
import it.unibo.pps.control.loader.configuration.dsl.SimulationDSL.*
import it.unibo.pps.control.loader.configuration.dsl.VirusDSL.*
import it.unibo.pps.control.loader.configuration.dsl.StructuresDSL.*
import it.unibo.pps.control.parser.ReaderModule.FilePath
import it.unibo.pps.entity.structure.entrance.Entrance.{
  BaseEntranceStrategy,
  EntranceStrategy,
  FilterBasedStrategy,
  ProbabilityBasedStrategy
}
import monix.eval.Task
import monocle.Focus.focus
import org.virtuslab.yaml.*

import scala.io.Source

object YAMLParser:

  trait Provider:
    val YAMLParser: Parser

  type Requirements = ReaderModule.Provider

  trait Component:
    context: Requirements =>
    class ParserImpl extends Parser:

      override def readFile(filePath: FilePath): Task[String] = context.readers.head.read(filePath)

      override def loadConfiguration(program: String): Task[Option[Configuration]] =
        for
          program <- Task(program.as[Map[String, Any]])
          result <- program match
            case Right(configurationParameters: Map[String, Any]) =>
              for
                simulation <- parseSimulationParameters(configurationParameters)
                virus <- parseVirusParameters(configurationParameters)
                structures <- parseStructuresParameters(configurationParameters)
              yield Some(VirsimConfiguration(simulation, virus, structures))
            case Left(_) => Task(None)
        yield result

      private def parseSimulationParameters(configurationParameters: Map[String, Any]): Task[Simulation] =
        var simulation: Simulation = Simulation()
        if configurationParameters.isDefinedAt("simulation") then
          val simulationParameters = configurationParameters("simulation").asInstanceOf[Map[String, Any]]
          simulationParameters.keys.foreach(key =>
            key match
              case "gridSide" =>
                simulation = simulation.gridSide(simulationParameters("gridSide").asInstanceOf[Int])
              case "days" =>
                simulation = simulation.days(simulationParameters("days").asInstanceOf[Int])
              case "entities" =>
                simulation = simulation.entities(simulationParameters("entities").asInstanceOf[Int])
              case "averagePopulationAge" =>
                simulation =
                  simulation.averagePopulationAge(simulationParameters("averagePopulationAge").asInstanceOf[Int])
              case "stdDevPopulationAge" =>
                simulation =
                  simulation.stdDevPopulationAge(simulationParameters("stdDevPopulationAge").asInstanceOf[Double])
              case "startingInfectedPercentage" =>
                simulation = simulation.startingInfectedPercentage(
                  simulationParameters("startingInfectedPercentage").asInstanceOf[Int]
                )
          )
        Task(simulation)

      private def parseVirusParameters(configurationParameters: Map[String, Any]): Task[Virus] =
        var virus: Virus = Virus()
        if configurationParameters.isDefinedAt("virus") then
          val virusParameters = configurationParameters("virus").asInstanceOf[Map[String, Any]]
          virusParameters.keys.foreach(key =>
            key match
              case "virusName" => virus = virus.virusName(virusParameters("virusName").asInstanceOf[String])
              case "spreadRate" => virus = virus.spreadRate(virusParameters("spreadRate").asInstanceOf[Double])
              case "averagePositivityDays" =>
                virus = virus.averagePositivityDays(virusParameters("averagePositivityDays").asInstanceOf[Int])
              case "stdDevPositivityDays" =>
                virus = virus.stdDevPositivityDays(virusParameters("stdDevPositivityDays").asInstanceOf[Double])
              case "severeDeseaseProbability" =>
                virus = virus.severeDeseaseProbability(virusParameters("severeDeseaseProbability").asInstanceOf[Double])
              case "maxInfectionDistance" =>
                virus = virus.maxInfectionDistance(virusParameters("maxInfectionDistance").asInstanceOf[Double])
          )
        Task(virus)

      private def parseStructuresParameters(configurationParameters: Map[String, Any]): Task[Set[SimulationStructure]] =
        var structures: Set[SimulationStructure] = Set.empty
        if configurationParameters.isDefinedAt("structures") then
          val structuresList = configurationParameters("structures").asInstanceOf[List[Map[String, Any]]]
          structuresList.foreach(structureMap =>
            structureMap.keys.foreach(key =>
              key match
                case "GenericBuilding" =>
                  val buildingParameters = structureMap("GenericBuilding").asInstanceOf[Map[String, Any]]
                  if buildingParameters.isDefinedAt("position") &&
                    buildingParameters.isDefinedAt("infectionProbability") &&
                    buildingParameters.isDefinedAt("capacity")
                  then
                    val position: List[Int] = buildingParameters("position").asInstanceOf[List[Int]]
                    val structure: GenericBuilding = GenericBuilding(
                      Point2D(
                        position.head * GlobalDefaults.GRID_MULTIPLIER,
                        position(1) * GlobalDefaults.GRID_MULTIPLIER
                      ),
                      buildingParameters("infectionProbability").asInstanceOf[Double],
                      buildingParameters("capacity").asInstanceOf[Int]
                    )
                    if buildingParameters.isDefinedAt("entranceStrategy") then
                      val strategy = buildingParameters("entranceStrategy").asInstanceOf[Map[String, Double]]
                      if strategy.head._1 == "probability" then
                        structures = structures + structure
                          .focus(_.entranceStrategy)
                          .replace(new BaseEntranceStrategy with ProbabilityBasedStrategy(strategy.head._2))
                      else if strategy.head._1 == "ageLowerThan" then
                        structures = structures + structure
                          .focus(_.entranceStrategy)
                          .replace(new BaseEntranceStrategy with FilterBasedStrategy(_.age < strategy.head._2))
                      else if strategy.head._1 == "ageGreaterThan" then
                        structures = structures + structure
                          .focus(_.entranceStrategy)
                          .replace(new BaseEntranceStrategy with FilterBasedStrategy(_.age > strategy.head._2))
                    else structures = structures + structure
                case "Hospital" =>
                  val hospitalParameters = structureMap("Hospital").asInstanceOf[Map[String, Any]]
                  if hospitalParameters.isDefinedAt("position") &&
                    hospitalParameters.isDefinedAt("infectionProbability") &&
                    hospitalParameters.isDefinedAt("capacity")
                  then
                    val position: List[Int] = hospitalParameters("position").asInstanceOf[List[Int]]
                    val structure: Hospital = Hospital(
                      Point2D(position.head, position(1)),
                      hospitalParameters("infectionProbability").asInstanceOf[Double],
                      hospitalParameters("capacity").asInstanceOf[Int]
                    )
                    if hospitalParameters.isDefinedAt("entranceStrategy") then
                      val strategy = hospitalParameters("entranceStrategy").asInstanceOf[Map[String, Double]]
                      if strategy.head._1 == "probability" then
                        structures = structures + structure
                          .focus(_.entranceStrategy)
                          .replace(new BaseEntranceStrategy with ProbabilityBasedStrategy(strategy.head._2))
                      else if strategy.head._1 == "ageLowerThan" then
                        structures = structures + structure
                          .focus(_.entranceStrategy)
                          .replace(new BaseEntranceStrategy with FilterBasedStrategy(_.age < strategy.head._2))
                      else if strategy.head._1 == "ageGreaterThan" then
                        structures = structures + structure
                          .focus(_.entranceStrategy)
                          .replace(new BaseEntranceStrategy with FilterBasedStrategy(_.age > strategy.head._2))
                    else structures = structures + structure
            )
          )
        Task(structures)

  trait Interface extends Provider with Component:
    self: Requirements =>

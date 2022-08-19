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
import monix.eval.Task
import org.virtuslab.yaml.*

import scala.io.Source

object YAMLParser:

  trait Provider:
    val YAMLParser: Parser

  trait Component:

    class ParserImpl extends Parser:

      override def readFile(path: String): Task[String] =
        for
          source <- Task(Source.fromFile(path))
          fileContent <- Task(source.mkString)
          _ <- Task(source.close())
        yield fileContent

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
              case "peoplePerHouse" =>
                simulation = simulation.peoplePerHouse(simulationParameters("peoplePerHouse").asInstanceOf[Int])
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
                    structures = structures + GenericBuilding(
                      Point2D(position.head, position(1)),
                      buildingParameters("infectionProbability").asInstanceOf[Double],
                      buildingParameters("capacity").asInstanceOf[Int]
                    )
                case "Hospital" =>
                  val hospitalParameters = structureMap("Hospital").asInstanceOf[Map[String, Any]]
                  if hospitalParameters.isDefinedAt("position") &&
                    hospitalParameters.isDefinedAt("infectionProbability") &&
                    hospitalParameters.isDefinedAt("capacity")
                  then
                    val position: List[Int] = hospitalParameters("position").asInstanceOf[List[Int]]
                    structures = structures + Hospital(
                      Point2D(position.head, position(1)),
                      hospitalParameters("infectionProbability").asInstanceOf[Double],
                      hospitalParameters("capacity").asInstanceOf[Int]
                    )
            )
          )
        Task(structures)

  trait Interface extends Provider with Component

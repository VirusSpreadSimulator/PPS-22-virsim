package it.unibo.pps.control.parser

import it.unibo.pps.control.engine.SimulationComponent.Simulation
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.{Configuration, VirsimConfiguration}
import it.unibo.pps.control.loader.configuration.SimulationDefaults.{GlobalDefaults, StructuresDefault}
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

/** The parser of configuration file in YAML format. */
object YAMLParser:

  /** Method to convert the value of the YAML parsing to configuration values. */
  extension (original: Any) def to[E] = original.asInstanceOf[E]

  /** Method that allows a better clarity in the source code. */
  extension [A, B](parameters: Map[A, B]) def has(key: A) = parameters.isDefinedAt(key)

  trait Provider:
    val YAMLParser: Parser

  type Requirements = ReaderModule.Provider

  trait Component:
    context: Requirements =>
    class ParserImpl extends Parser:

      override def readFile(filePath: FilePath): Task[String] = context.reader.read(filePath)

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
        if configurationParameters.has("simulation") then
          val simulationParameters = configurationParameters("simulation").to[Map[String, Any]]
          simulationParameters.keys.foreach(key =>
            key match
              case "gridSide" =>
                simulation = simulation.gridSide(simulationParameters("gridSide").to[Int])
              case "days" =>
                simulation = simulation.days(simulationParameters("days").to[Int])
              case "entities" =>
                simulation = simulation.entities(simulationParameters("entities").to[Int])
              case "averagePopulationAge" =>
                simulation = simulation.averagePopulationAge(simulationParameters("averagePopulationAge").to[Int])
              case "stdDevPopulationAge" =>
                simulation = simulation.stdDevPopulationAge(simulationParameters("stdDevPopulationAge").to[Double])
              case "startingInfectedPercentage" =>
                simulation = simulation.startingInfectedPercentage(
                  simulationParameters("startingInfectedPercentage").to[Int]
                )
          )
        Task(simulation)

      private def parseVirusParameters(configurationParameters: Map[String, Any]): Task[Virus] =
        var virus: Virus = Virus()
        if configurationParameters.has("virus") then
          val virusParameters = configurationParameters("virus").to[Map[String, Any]]
          virusParameters.keys.foreach(key =>
            key match
              case "virusName" => virus = virus.virusName(virusParameters("virusName").to[String])
              case "spreadRate" => virus = virus.spreadRate(virusParameters("spreadRate").to[Double])
              case "averagePositivityDays" =>
                virus = virus.averagePositivityDays(virusParameters("averagePositivityDays").to[Int])
              case "stdDevPositivityDays" =>
                virus = virus.stdDevPositivityDays(virusParameters("stdDevPositivityDays").to[Double])
              case "severeDeseaseProbability" =>
                virus = virus.severeDeseaseProbability(virusParameters("severeDeseaseProbability").to[Double])
              case "maxInfectionDistance" =>
                virus = virus.maxInfectionDistance(virusParameters("maxInfectionDistance").to[Double])
          )
        Task(virus)

      private def parseEntranceStrategy(parameters: Map[String, Any]): EntranceStrategy =
        if parameters.has("entranceStrategy") then
          val strategy: Map[String, Double] = parameters("entranceStrategy").to[Map[String, Double]]
          if strategy.head._1 == "probability" then
            new BaseEntranceStrategy with ProbabilityBasedStrategy(strategy.head._2)
          else if strategy.head._1 == "ageLowerThan" then
            new BaseEntranceStrategy with FilterBasedStrategy(_.age < strategy.head._2)
          else if strategy.head._1 == "ageGreaterThan" then
            new BaseEntranceStrategy with FilterBasedStrategy(_.age > strategy.head._2)
          else new BaseEntranceStrategy()
        else new BaseEntranceStrategy()

      private def parseSingleStructure(structureParameters: Map[String, Any]): Task[SimulationStructure] =
        for
          _ <- Task.pure {}
          isGenericBuilding = structureParameters.has("GenericBuilding")
          parameters =
            if isGenericBuilding then structureParameters("GenericBuilding").to[Map[String, Any]]
            else structureParameters("Hospital").to[Map[String, Any]]
          positionsList = parameters("position").to[List[Int]]
          position = Point2D(positionsList.head, positionsList(1))
          infectionProbability = parameters("infectionProbability").to[Double]
          capacity = parameters("capacity").to[Int]
          entranceStrategy = parseEntranceStrategy(parameters)
          visibility =
            if parameters.has("visibilityDistance") then parameters("visibilityDistance").to[Double]
            else StructuresDefault.DEFAULT_VISIBILITY_DISTANCE
          group =
            if parameters.has("group") then parameters("group").to[String]
            else StructuresDefault.DEFAULT_GROUP
        yield
          if isGenericBuilding then
            GenericBuilding(position, infectionProbability, capacity, entranceStrategy = entranceStrategy,
              visibilityDistance = visibility, group = group)
          else
            Hospital(
              position,
              infectionProbability,
              capacity,
              entranceStrategy = entranceStrategy,
              visibilityDistance = visibility
            )

      private def parseStructuresParameters(configurationParameters: Map[String, Any]): Task[Set[SimulationStructure]] =
        for
          isDefined <- Task(configurationParameters.has("structures"))
          structuresList =
            if isDefined then configurationParameters("structures").to[List[Map[String, Any]]] else List.empty
          structures <- Task.sequence {
            if isDefined then for structure <- structuresList yield parseSingleStructure(structure)
            else List.empty[Task[SimulationStructure]]
          }
        yield structures.toSet

  trait Interface extends Provider with Component:
    self: Requirements =>

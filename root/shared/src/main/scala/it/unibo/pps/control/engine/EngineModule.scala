package it.unibo.pps.control.engine

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.Hit
import it.unibo.pps.control.engine.config.EngineConfiguration.SimulationConfig
import it.unibo.pps.control.loader.configuration.SimulationDefaults.GlobalDefaults
import it.unibo.pps.entity.State
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import monix.catnap.ConcurrentQueue
import monix.eval.Task
import monix.reactive.Observable

import scala.concurrent.duration.{FiniteDuration, TimeUnit}

object EngineModule:
  trait Engine: // todo: maybe 'init' it is not useful, considering that the duration of the simulation can be pass
    def init(simulationDuration: Int): Unit
    def startSimulationLoop(environment: Environment): Task[Unit]
  trait Provider:
    val engine: Engine
  type Requirements = BoundaryModule.Provider
  trait Component:
    context: Requirements =>
    class EngineImpl(using simulationConfiguration: SimulationConfig) extends Engine:
      import EngineImpl.given

      private var simulationDuration: Int = GlobalDefaults.DURATION

      override def init(duration: Int): Unit =
        simulationDuration = duration

      override def startSimulationLoop(environment: Environment): Task[Unit] =
        simulationDispatcher(environment)

      private def simulationDispatcher(environment: Environment): Task[Unit] =
        for
          eventQueue <- ConcurrentQueue.unbounded[Task, Event]()
          sinkEvent = Observable
            .fromIterable(context.boundaries)
            .mergeMap(_.events())
            .mapEval(event => eventQueue.offer(event))
            .foreachL { _ => }
          simulationTask <- Task.parMap2(sinkEvent, simulationLoop(eventQueue, environment)) { (_, _) => }
        //_ <- Task(context.boundaries.main.startSimulation(gridSide))
        yield simulationTask

      private def simulationLoop(queue: ConcurrentQueue[Task, Event], environment: Environment): Task[Unit] =
        for
          events <- queue.drain(0, simulationConfiguration.maxEventPerIteration)
          timeTarget = simulationConfiguration.engineSpeed.tickTime
          prevTime <- timeNow(timeTarget.unit)
//          currentState <- getCurrentState()
//          newState <- handleEvents(currentState, events.toList)
//          _ <- updateEnv(newState)
          _ <- debugEvents(events)
          env = createTestEnv()
          _ <- renderBoundaries(env).asyncBoundary // Render and return to default scheduler with asyncBoundary
          newTime <- timeNow(timeTarget.unit)
          timeDiff = FiniteDuration(newTime - prevTime, timeTarget.unit)
          _ <- waitNextTick(timeDiff, timeTarget)
          _ <- simulationLoop(queue, environment)
        yield ()

      private def createTestEnv(): Environment = //todo: to be deleted
        import it.unibo.pps.entity.common.Space.Point2D
        import it.unibo.pps.entity.entity.Entities.BaseEntity
        import it.unibo.pps.entity.structure.Structures.House
        import it.unibo.pps.entity.environment.EnvironmentModule.Component
        import it.unibo.pps.entity.common.GaussianProperty.GaussianIntDistribution
        import it.unibo.pps.entity.structure.StructureComponent.Hospitalization.TreatmentQuality
        import it.unibo.pps.entity.virus.VirusComponent.Virus
        import it.unibo.pps.entity.entity.Entities.SimulationEntity
        import it.unibo.pps.entity.environment.EnvironmentModule
        import it.unibo.pps.entity.environment.EnvironmentModule.Component
        import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, SimulationStructure}
        import scala.util.Random

        val nEntities = 16
        val ageDistribution = GaussianIntDistribution(27, 10)
        val gridSize = 30
        val peoplePerHouse = 4
        // Houses
        val houses =
          for i <- 0 until nEntities / peoplePerHouse
          yield House((Random.nextInt(gridSize), Random.nextInt(gridSize)), 0.5, peoplePerHouse)
        val generic = GenericBuilding((Random.nextInt(gridSize), Random.nextInt(gridSize)), 0.5, 10)
        val hospital = Hospital(
          (Random.nextInt(gridSize), Random.nextInt(gridSize)),
          0.5,
          10,
          treatmentQuality = TreatmentQuality.GOOD
        )
        val entities = for
          i <- 0 until 15
          entity = BaseEntity(
            i,
            ageDistribution.next(),
            houses(i % peoplePerHouse),
            position = Point2D(Random.nextInt(gridSize), Random.nextInt(gridSize))
          )
        yield entity
        object prova extends EnvironmentModule.Interface:
          override val env = Environment.empty
        prova.env.initialized(gridSize, entities.toSet, Virus(), (generic +: hospital +: houses).toSet)

      private def debugEvents(events: Seq[Event]): Task[Unit] = events match
        case event +: t =>
          Task {
            println(event)
            debugEvents(t)
          }
        case _ => Task.pure {}
//      // todo: need to do a process of re-engineering in handling of events and update logic (considering the model).
//      private def handleEvents(state: State, events: Seq[Event]): Task[State] = events match
//        case event :: t => handleEvents(handleLogic(state, event), t)
//        case _ =>
//          computeNewState(state) // At the end (we are sure that the tick time passed in the loop thanks to the wait)

//      private def getCurrentState(): Task[State] =
//        Task.eval(context.env.getState())
//
//      private def updateEnv(state: State): Task[Unit] =
//        Task.eval(context.env.updateState(state))

      private def renderBoundaries(env: Environment): Task[Seq[Unit]] =
        Task.sequence(context.boundaries.map(_.consume(env)))

      private def timeNow(unit: TimeUnit): Task[Long] = Task.clock.monotonic(unit)

      private def waitNextTick(timeDiff: FiniteDuration, timeTarget: FiniteDuration): Task[Unit] = timeDiff match
        case n if n > timeTarget => Task.pure {}
        case _ => Task.sleep(timeTarget - timeDiff)

      // Simulation Logic
      private def handleLogic(state: State, event: Event): State = event match
        case Hit(n) => println("HIT"); State(0)

//      private def computeNewState(state: State): State = state match
//        case State(number) if number < max => State(number + 1)
//        case _ => State(0)

    object EngineImpl:
      given Conversion[State, Task[State]] = Task(_)

  trait Interface extends Provider with Component:
    self: Requirements =>

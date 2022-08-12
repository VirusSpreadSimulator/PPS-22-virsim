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
        yield simulationTask

      private def simulationLoop(queue: ConcurrentQueue[Task, Event], environment: Environment): Task[Unit] =
        for
          events <- queue.drain(0, simulationConfiguration.maxEventPerIteration)
          timeTarget = simulationConfiguration.engineSpeed.tickTime
          prevTime <- timeNow(timeTarget.unit)
          _ <- debugEvents(events)
          envAfterEvents <- handleEvents(events, environment)
          updatedEnv <- performLogics(envAfterEvents)
          _ <- renderBoundaries(updatedEnv)
          newTime <- timeNow(timeTarget.unit)
          timeDiff = FiniteDuration(newTime - prevTime, timeTarget.unit)
          _ <- waitNextTick(timeDiff, timeTarget)
          _ <- simulationLoop(queue, environment)
        yield ()

      private def performLogics(environment: Environment): Task[Environment] =
        simulationConfiguration.logics.foldLeft(Task(environment))((t, logic) => t.flatMap(env => logic(env)))

      private def handleEvents(events: Seq[Event], environment: Environment): Task[Environment] =
        events
          .map(event => simulationConfiguration.eventLogics(event))
          .foldLeft(Task(environment))((t, logic) => t.flatMap(env => logic(env)))

      private def debugEvents(events: Seq[Event]): Task[Unit] =
        if events.nonEmpty then Task(println(events.foldLeft("Event processed:")(_ + " " + _))) else Task.pure {}

      private def renderBoundaries(env: Environment): Task[Seq[Unit]] =
        Task.sequence(context.boundaries.map(_.consume(env)))

      private def timeNow(unit: TimeUnit): Task[Long] = Task.clock.monotonic(unit)

      private def waitNextTick(timeDiff: FiniteDuration, timeTarget: FiniteDuration): Task[Unit] = timeDiff match
        case n if n > timeTarget => Task.pure {}
        case _ => Task.sleep(timeTarget - timeDiff)

    object EngineImpl:
      given Conversion[State, Task[State]] = Task(_)

  trait Interface extends Provider with Component:
    self: Requirements =>

package it.unibo.pps.control.engine

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.control.engine.config.EngineConfiguration.SimulationConfig
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.control.engine.config.Configurations.EngineStatus
import scala.concurrent.duration.{FiniteDuration, TimeUnit}
import monix.catnap.ConcurrentQueue
import monix.eval.Task
import monix.reactive.Observable

object EngineModule:
  /** The engine is the component that is responsible for the actual simulation. */
  trait Engine:
    /** Method to start the simulation loop.
      * @param environment
      *   the start environment
      * @return
      *   the task
      */
    def startSimulationLoop(environment: Environment): Task[Unit]
  /** Provider of the component. */
  trait Provider:
    val engine: Engine
  /** It requires the boundaries in order to be able to include their update in the loop. */
  type Requirements = BoundaryModule.Provider
  /** Engine component. */
  trait Component:
    context: Requirements =>
    /** Implementation of the engine component.
      * @param config
      *   the simulation configuration
      */
    class EngineImpl(using config: SimulationConfig) extends Engine:

      override def startSimulationLoop(environment: Environment): Task[Unit] =
        simulationDispatcher(environment)

      /** It allow to start and dispatch all the necessary elements for the simulation loop.
        * @param environment
        *   the start environment
        * @return
        *   the task
        */
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

      /** It's the real simulation loop.
        * @param queue
        *   the event queue
        * @param environment
        *   the current environment
        * @return
        *   the task
        */
      private def simulationLoop(queue: ConcurrentQueue[Task, Event], environment: Environment): Task[Unit] =
        for
          events <- queue.drain(0, config.maxEventPerIteration)
          timeTarget = config.engineSpeed.tickTime
          prevTime <- timeNow(timeTarget.unit)
          _ <- debugEvents(events) // todo: to be deleted, only to see events
          envAfterEvents <- handleEvents(events, environment)
          updatedEnv <- performLogics(envAfterEvents)
          _ <- Task(println(updatedEnv.time)) //todo: to be deleted, only to see the progress
          _ <- renderBoundaries(updatedEnv)
          newTime <- timeNow(timeTarget.unit)
          timeDiff = FiniteDuration(newTime - prevTime, timeTarget.unit)
          _ <- waitNextTick(timeDiff, timeTarget)
          _ <- if config.engineStatus != EngineStatus.STOPPED then simulationLoop(queue, updatedEnv) else stop()
        yield ()

      /** Method to perform all the required logics on the environment.
        * @param environment
        *   the current environment
        * @return
        *   the task
        */
      private def performLogics(environment: Environment): Task[Environment] = config.engineStatus match
        case EngineStatus.RUNNING =>
          config.logics.foldLeft(Task(environment))((t, logic) => t.flatMap(env => logic(env)))
        case _ => Task(environment)

      /** Handle the events performing computation on the current environment.
        * @param events
        *   the events to process
        * @param environment
        *   the current environment
        * @return
        *   the task
        */
      private def handleEvents(events: Seq[Event], environment: Environment): Task[Environment] =
        events
          .filter(event => event.interested(config.engineStatus))
          .map(event => config.eventLogics(event))
          .foldLeft(Task(environment))((t, logic) => t.flatMap(env => logic(env)))

      private def debugEvents(events: Seq[Event]): Task[Unit] =
        if events.nonEmpty then Task(println(events.foldLeft("Event processed:")(_ + " " + _))) else Task.pure {}

      /** Method to allow the boundaries to consume the current state of the environment.
        * @param env
        *   the current environment
        * @return
        *   the task
        */
      private def renderBoundaries(env: Environment): Task[Unit] =
        Task.sequence(context.boundaries.map(_.consume(env))).foreachL { _ => }

      /** Method to handle the stop of the simulation.
        * @return
        *   the task
        */
      private def stop(): Task[Unit] =
        Task.sequence(context.boundaries.map(_.stop())).foreachL { _ => }

      /** Method to obtain the time in the current instant.
        * @param unit
        *   the time unit of the time to return
        * @return
        *   the task containing the time
        */
      private def timeNow(unit: TimeUnit): Task[Long] = Task.clock.monotonic(unit)

      /** Method to wait for the next simulation tick.
        * @param timeDiff
        *   the time elapsed during this tick
        * @param timeTarget
        *   the target time for the tick
        * @return
        *   the task that allow to wait
        */
      private def waitNextTick(timeDiff: FiniteDuration, timeTarget: FiniteDuration): Task[Unit] = timeDiff match
        case n if n > timeTarget => Task.pure {}
        case _ => Task.sleep(timeTarget - timeDiff)
  /** The interface of the component. */
  trait Interface extends Provider with Component:
    self: Requirements =>

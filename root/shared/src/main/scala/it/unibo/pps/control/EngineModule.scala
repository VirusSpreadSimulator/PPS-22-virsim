package it.unibo.pps.control

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.entity.{EnvModule, State}
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{Observable, OverflowStrategy}
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}

object EngineModule:
  trait Engine:
    def init(maxNumber: Int): Unit
    def startSimulation(): Unit
  trait Provider:
    val engine: Engine
  type Requirements = BoundaryModule.Provider with EnvModule.Provider
  trait Component:
    context: Requirements =>
    class EngineImpl extends Engine:
      import EngineImpl.given
      import EngineImpl.time

      private var max: Int = 0 // todo: brutto forse?
      override def init(maxNumber: Int): Unit =
        max = maxNumber
      override def startSimulation(): Unit =
        given Scheduler = monix.execution.Scheduler.global
        simulationReactLoop().runAsyncAndForget
//        simulationStep()
//          .loopForever
//          .runAsyncAndForget

      private def simulationReactLoop(): Task[Unit] =
        given OverflowStrategy[Event] = OverflowStrategy.Default
        Observable(time, Observable.fromIterable(context.boundaries).flatMap(_.events())).merge
          .scanEval(getCurrentState())((state, event) => handleLogic(state, event))
          .doOnNext(state =>
            for
              _ <- updateEnv(state)
              _ <- renderBoundaries(state)
            yield ()
          )
          .completedL
//        for
//          currentState <- getCurrentState()
//          newState <- computeNewState(currentState)
//          _ <- updateEnv(newState)
//          render <- renderBoundaries(newState)
//        yield ()

      private def handleLogic(state: State, event: Event): Task[State] = event match
        case Time() => computeNewState(state)
        case Hit(n) => println("HIT"); Task(State(0))

      private def getCurrentState(): Task[State] =
        Task.eval(context.env.getState())

      private def computeNewState(state: State): Task[State] = state match
        case State(number) if number < max => State(number + 1)
        case _ => State(0)

      private def updateEnv(state: State): Task[Unit] =
        Task.eval(context.env.updateState(state))

      private def renderBoundaries(state: State): Task[Seq[Unit]] =
        Task.sequence(context.boundaries.map(_.render(state.number)))

    object EngineImpl:
      given Conversion[State, Task[State]] = Task(_)

      private val time: Observable[Event] = Observable
        .fromIterable(LazyList.continually(1))
        .delayOnNext(FiniteDuration(100, MILLISECONDS))
        .map(_ => Time())

  trait Interface extends Provider with Component:
    self: Requirements =>

package it.unibo.pps.control

import monix.eval.Task

object LoaderModule:
  trait Loader:
    def load(configMax: Int): Task[Unit]
  trait Provider:
    val loader: Loader
  type Requirements = EngineModule.Provider
  trait Component:
    context: Requirements =>
    class LoaderImpl extends Loader:
      override def load(configMax: Int): Task[Unit] =
        for
          _ <- Task(context.engine.init(configMax))
          _ <- context.engine.startSimulation()
        yield ()
  trait Interface extends Provider with Component:
    self: Requirements =>

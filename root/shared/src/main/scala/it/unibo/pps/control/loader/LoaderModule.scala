package it.unibo.pps.control.loader

import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.Configuration
import it.unibo.pps.control.loader.configuration.SimulationConfigurations.SimulationConfiguration
import it.unibo.pps.control.loader.configuration.VirusConfigurations.VirusConfiguration

import it.unibo.pps.entity.EnvModule.Env
import monix.eval.Task

object LoaderModule:
  trait Loader:
    /** @param configurationFile
      *   the configuration file with simulation parameters.
      * @return
      *   a configuration of simulation, virus and structures.
      */
    def load(configurationFile: String): Configuration
  trait Provider:
    val loader: Loader
  type Requirements = EngineModule.Provider
  trait Component:
    context: Requirements =>
    class LoaderImpl extends Loader:
      override def load(configurationFile: String): Configuration = ???
      for
        configuration <- Task(load)
        _ <- Task(context.engine.init(0))
        _ <- context.engine.startSimulation()
      yield ()
  trait Interface extends Provider with Component:
    self: Requirements =>

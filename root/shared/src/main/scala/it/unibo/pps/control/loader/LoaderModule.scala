package it.unibo.pps.control.loader

import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.Configuration
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.VirsimConfiguration

import it.unibo.pps.entity.EnvModule.Env
import monix.eval.Task

import javax.script.ScriptEngineManager
import scala.io.Source

object LoaderModule:
  trait Loader:

    /** @param configurationFile
      *   the configuration file with simulation parameters.
      * @return
      *   a configuration of simulation, virus and structures.
      */
    def load(configurationFile: String): Configuration

    /** @param configuration
      *   The configuration of the simulation, structures and virus.
      * @return
      *   the initialized environment.
      */
    def createEnvironment(configuration: Configuration): Env

  trait Provider:
    val loader: Loader
  type Requirements = EngineModule.Provider

  trait Component:
    context: Requirements =>
    class LoaderImpl extends Loader:
      override def load(configurationFile: String): Configuration =
        val engineManager: ScriptEngineManager = new ScriptEngineManager(getClass().getClassLoader())
        val engine = engineManager.getEngineByName("scala")
        engine
          .eval(Source.fromFile(getClass.getResource("configuration.scala").getFile).getLines.mkString)
          .asInstanceOf[VirsimConfiguration]

      override def createEnvironment(configuration: Configuration): Env = ???
      for
        configuration <- Task(load)
        initializedEnvironment <- Task(createEnvironment)
        _ <- Task(context.engine.init(0)) // pass environment instead of maxNumber
        _ <- context.engine.startSimulation()
      yield ()
  trait Interface extends Provider with Component:
    self: Requirements =>

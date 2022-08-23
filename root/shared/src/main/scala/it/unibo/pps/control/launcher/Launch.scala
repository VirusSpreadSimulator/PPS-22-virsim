package it.unibo.pps.control.launcher

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.BoundaryModule.Boundary
import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.engine.EngineModule.Engine
import it.unibo.pps.control.engine.config.EngineConfiguration
import it.unibo.pps.control.launcher.LauncherModule
import it.unibo.pps.control.launcher.LauncherModule.Launcher
import it.unibo.pps.control.parser.ReaderModule
import it.unibo.pps.control.loader.LoaderModule
import it.unibo.pps.control.loader.LoaderModule.Loader
import it.unibo.pps.control.parser.ParserModule
import it.unibo.pps.control.parser.ParserModule.Parser
import it.unibo.pps.entity.environment.EnvironmentModule
import it.unibo.pps.entity.environment.EnvironmentModule.Environment

/** Launch base config (End of the world) */
trait Launch
    extends BoundaryModule.Interface
    with LauncherModule.Interface
    with LoaderModule.Interface
    with ParserModule.Interface
    with EngineModule.Interface
    with EnvironmentModule.Interface
    with ReaderModule.Interface:

  import it.unibo.pps.control.engine.config.EngineConfiguration.given

  override val env: Environment = Environment.empty
  override val engine: Engine = EngineImpl()
  override val loader: Loader = LoaderImpl()
  override val launcher: Launcher = LauncherImpl()

  def launch(): Unit = launcher.launch().runAsyncAndForget

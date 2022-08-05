package it.unibo.pps.control.launcher

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.launcher.LauncherModule
import it.unibo.pps.control.loader.LoaderModule
import it.unibo.pps.control.engine.EngineConfiguration
import it.unibo.pps.entity.environment.EnvironmentModule

/** Launch base config (End of the world) */
trait Launch
    extends BoundaryModule.Interface
    with LauncherModule.Interface
    with LoaderModule.Interface
    with EngineModule.Interface
    with EnvironmentModule.Interface:

  import EngineConfiguration.given

  override val env = EnvironmentImpl()
  override val engine = EngineImpl()
  override val loader = LoaderImpl()
  override val launcher = LauncherImpl()

  def launch(): Unit = launcher.launch().runAsyncAndForget

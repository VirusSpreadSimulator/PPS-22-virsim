package it.unibo.pps.control

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.{EngineModule, LauncherModule, LoaderModule}
import it.unibo.pps.entity.EnvModule
import monix.execution.Scheduler
import scala.concurrent.duration.DurationInt

/** Launch base config (End of the world) */
trait Launch
    extends BoundaryModule.Interface
    with LauncherModule.Interface
    with LoaderModule.Interface
    with EngineModule.Interface
    with EnvModule.Interface:

  import EngineConfiguration.given

  override val env = EnvImpl()
  override val engine = EngineImpl()
  override val loader = LoaderImpl()
  override val launcher = LauncherImpl()

  def launch(): Unit = launcher.launch().runAsyncAndForget

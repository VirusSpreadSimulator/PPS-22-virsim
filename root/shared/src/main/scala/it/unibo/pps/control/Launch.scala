package it.unibo.pps.control

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.control.{EngineModule, LauncherModule, LoaderModule}
import it.unibo.pps.entity.EnvModule
import monix.execution.Scheduler

trait Launch
    extends BoundaryModule.Interface
    with LauncherModule.Interface
    with LoaderModule.Interface
    with EngineModule.Interface
    with EnvModule.Interface:

  override val env = EnvImpl()
  override val engine = EngineImpl()
  override val loader = LoaderImpl()
  override val launcher = LauncherImpl()

  given Scheduler = monix.execution.Scheduler.global

  def launch(): Unit = launcher.launch().runAsyncAndForget

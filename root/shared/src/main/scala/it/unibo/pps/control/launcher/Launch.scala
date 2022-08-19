package it.unibo.pps.control.launcher

import it.unibo.pps.boundary.BoundaryModule
import it.unibo.pps.boundary.exporter.ExporterModule
import it.unibo.pps.boundary.exporter.Extractors.{Days, Deaths, HospitalFreeSeats, Sick}
import it.unibo.pps.control.engine.EngineModule
import it.unibo.pps.control.engine.config.EngineConfiguration
import it.unibo.pps.control.launcher.LauncherModule
import it.unibo.pps.control.loader.LoaderModule
import it.unibo.pps.control.parser.ParserModule
import it.unibo.pps.entity.environment.EnvironmentModule

/** Launch base config (End of the world) */
trait Launch
    extends BoundaryModule.Interface
    with LauncherModule.Interface
    with LoaderModule.Interface
    with ParserModule.Interface
    with EngineModule.Interface
    with EnvironmentModule.Interface
    with ExporterModule.Interface:

  import it.unibo.pps.control.engine.config.EngineConfiguration.given

  override val env = Environment.empty
  override val engine = EngineImpl()
  override val loader = LoaderImpl()
  override val launcher = LauncherImpl()
  override val exporter = FileExporterImpl()
  override val boundaries = Seq(exporter)

  def launch(): Unit = launcher.launch().runAsyncAndForget

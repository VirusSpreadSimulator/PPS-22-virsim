package it.unibo.pps.js.boundary.gui.panel

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.js.boundary.gui.Values.SimulationColor
import it.unibo.pps.boundary.component.panel.Panels.UpdatablePanel
import monix.eval.Task
import org.scalajs.dom

/** The Simulation Canvas is the panel that handle the visualization of the simulation status. */
class SimulationCanvas extends UpdatablePanel:
  private lazy val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
  private lazy val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  override def init(): Task[Unit] = for
    _ <- io(dom.document.getElementById("simulation").appendChild(canvas))
    _ <- io(setCanvasSize())
    _ <- io(dom.window.onresize = _ => setCanvasSize())
  yield ()

  override def update(newEnv: Environment): Task[Unit] =
    import it.unibo.pps.js.boundary.gui.component.drawable.Drawables.given
    for
      envDim <- io(newEnv.gridSide)
      scale <- io(Math.max(Math.min(canvas.width / envDim, canvas.height / (envDim + 1)), 1)) // to be square
      _ <- io(ctx.fillStyle = SimulationColor.BACKGROUND_COLOR)
      _ <- io(ctx.fillRect(0, 0, envDim * scale, (envDim + 1) * scale))
      _ <- io(newEnv.draw(ctx, scale))
    yield ()

  private def setCanvasSize(): Unit =
    canvas.width = canvas.parentElement.clientWidth
    canvas.height = (dom.window.innerHeight - canvas.offsetTop).toInt

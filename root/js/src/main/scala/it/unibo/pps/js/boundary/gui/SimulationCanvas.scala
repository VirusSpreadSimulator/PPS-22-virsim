package it.unibo.pps.js.boundary.gui

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.js.boundary.Values.SimulationColor
import it.unibo.pps.js.boundary.gui.Panels.UpdatablePanel
import monix.eval.Task
import org.scalajs.dom

class SimulationCanvas extends UpdatablePanel:
  private val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
  private val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  override def init(): Task[Unit] = for
    _ <- io(dom.document.getElementById("simulation").appendChild(canvas))
    _ <- io(setCanvasSize())
    _ <- io(dom.window.onresize = e => setCanvasSize())
  yield ()

  override def update(newEnv: Environment): Task[Unit] =
    import it.unibo.pps.js.boundary.component.drawable.Drawables.given
    for
      _ <- io(ctx.fillStyle = SimulationColor.BACKGROUND_COLOR)
      _ <- io(ctx.fillRect(0, 0, canvas.width, canvas.height))
      envDim <- io(newEnv.gridSide)
      scale <- io(Math.max(Math.min(canvas.width / envDim, canvas.height / (envDim + 1)), 1)) // to be square
      _ <- io(newEnv.draw(ctx, scale))
    yield ()

  private def setCanvasSize(): Unit =
    canvas.width = canvas.parentElement.clientWidth
    canvas.height = (dom.window.innerHeight - canvas.offsetTop).toInt

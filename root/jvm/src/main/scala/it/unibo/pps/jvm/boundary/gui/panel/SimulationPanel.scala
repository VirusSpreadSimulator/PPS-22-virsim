package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.gui.panel.Panels.UpdateblePanel

import java.awt.{Graphics, Graphics2D, RenderingHints}
import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.jvm.boundary.Values.SimulationColor
import monix.eval.Task

/** The Simulation Panel is the panel that handle the visualization of the simulation status. For this reason it extends
  * [[UpdateblePanel]]
  */
class SimulationPanel() extends UpdateblePanel:
  private var env: Option[Environment] = None

  override def paintComponent(g: Graphics): Unit =
    super.paintComponent(g)
    setBackground(SimulationColor.BACKGROUND_COLOR)
    if env.isDefined then
      val environment = env.get
      val panelDim = getSize()
      val envDim = environment.gridSide
      val scale = Math.max(Math.min(panelDim.width / envDim, panelDim.height / envDim), 1) // to be square
      val g2: Graphics2D = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

      import it.unibo.pps.jvm.boundary.component.Drawables.given
      environment.draw(g2, scale)

  override def init(): Task[Unit] = io(setOpaque(true))

  override def update(newEnv: Environment): Task[Unit] = for
    _ <- io { env = Some(newEnv) }
    _ <- io(this.repaint())
  yield ()

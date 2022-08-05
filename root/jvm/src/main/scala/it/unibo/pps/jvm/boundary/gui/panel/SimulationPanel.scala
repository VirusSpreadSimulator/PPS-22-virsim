package it.unibo.pps.jvm.boundary.gui.panel

import java.awt.geom.AffineTransform
import javax.swing.JPanel
import java.awt.{Color, Dimension, Graphics, Graphics2D, RenderingHints}
import scala.util.Random

class SimulationPanel() extends UpdateblePanel:
  override def paint(g: Graphics): Unit =
    val panelDim = getSize()
    val envDim = (100, 100)
    val scale = Math.max(Math.min(panelDim.width / envDim._1, panelDim.height / envDim._2), 1) // to be square
    val g2: Graphics2D = g.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    for
      i <- 0 to envDim._1
      rnd = Random.nextInt(2)
      color = if rnd % 2 == 0 then Color.RED else Color.GREEN
    do
      g2.setColor(color)
      g2.fillOval((i * scale) - scale, (i * scale) - scale, 1 * scale, 1 * scale)

  override def getPreferredSize: Dimension =
    import java.awt.Dimension
    import it.unibo.pps.jvm.boundary.Utils.given
    var d = super.getPreferredSize
    val c = getParent
    if c != null then d = c.getSize
    val s: Int = Math.min(d.getWidth.toInt, d.getHeight.toInt)
    (s, s)

  override def updateAndDisplay(): Unit = this.repaint()

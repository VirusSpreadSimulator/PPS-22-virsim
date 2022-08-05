package it.unibo.pps.jvm.boundary.gui.panel

import java.awt.geom.AffineTransform
import javax.swing.JPanel
import java.awt.{Color, Dimension, Graphics, Graphics2D, RenderingHints}

trait SimulationPanel extends JPanel:
  def display(): Unit

object SimulationPanel:
  def apply(): SimulationPanel = SimulationPanelImpl()
  private class SimulationPanelImpl() extends SimulationPanel:
    override def paint(g: Graphics): Unit =
      val panelDim = getSize()
      println(panelDim)
      val envDim = (100, 100)
      val scale = (Math.max(panelDim.width / envDim._1, 1), Math.max(panelDim.height / envDim._2, 1))
      val g2: Graphics2D = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      for
        i <- 0 to envDim._1
        color = if i % 2 == 0 then Color.RED else Color.GREEN
      do
        g2.setColor(color)
        println((i * scale._1) - scale._1)
        g2.fillOval((i * scale._1) - scale._1, (i * scale._2) - scale._2, 1 * scale._1, 1 * scale._2)

    override def getPreferredSize: Dimension =
      import java.awt.Dimension
      import it.unibo.pps.jvm.boundary.Utils.given
      var d = super.getPreferredSize
      val c = getParent
      if c != null then d = c.getSize
      val s: Int = Math.min(d.getWidth.toInt, d.getHeight.toInt)
      (s, s)
    override def display(): Unit = repaint()

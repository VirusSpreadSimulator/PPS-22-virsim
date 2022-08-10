package it.unibo.pps.jvm.boundary.gui.panel

import it.unibo.pps.jvm.boundary.gui.panel.Panels.UpdateblePanel

import java.awt.geom.AffineTransform
import javax.swing.JPanel
import java.awt.{Color, Dimension, Font, Graphics, Graphics2D, RenderingHints}
import scala.util.Random
import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.entity.entity.Entities.BaseEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Visible
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
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

      drawStructures(g2, environment, scale)
      drawEntities(g2, environment, scale)

  private def drawStructures(g: Graphics2D, environment: Environment, scale: Int): Unit =
    for struct <- environment.structures do drawStructure(g, struct, scale)

  private def drawStructure(g: Graphics2D, structure: SimulationStructure, scale: Int): Unit =
    structure match
      case house: House =>
        g.setColor(SimulationColor.HOUSE_COLOR)
      case generic: GenericBuilding =>
        drawStructureVisibility(g, generic, scale)
        g.setColor(if generic.isOpen then SimulationColor.GENERIC_COLOR_OPEN else SimulationColor.GENERIC_COLOR_CLOSED)
      case hospital: Hospital =>
        drawStructureVisibility(g, hospital, scale)
        g.setColor(SimulationColor.HOSPITAL_COLOR)
    g.fillRect(scaleToView(structure.position.x, scale), scaleToView(structure.position.y, scale), scale, scale)
    drawRemainedCapacity(g, structure, scale)

  private def drawRemainedCapacity(g: Graphics2D, structure: SimulationStructure, scale: Int): Unit =
    val message = s"${structure.capacity - structure.entities.size}"
    val font = Font(Font.SANS_SERIF, Font.PLAIN, 10)
    val scaleFont = AffineTransform()
    val fontMetrics = g.getFontMetrics(font)
    val scaleF = Math.min(
      scale.toDouble / (fontMetrics.getMaxAscent + fontMetrics.getMaxDescent),
      scale.toDouble / fontMetrics.stringWidth(message)
    )
    scaleFont.scale(scaleF, scaleF)
    g.setFont(font.deriveFont(scaleFont))
    g.setColor(Color.WHITE)
    g.drawString(
      message,
      scaleToView(structure.position.x, scale),
      scaleToView(structure.position.y + 1, scale)
    )

  private def drawStructureVisibility(g: Graphics2D, structure: SimulationStructure with Visible, scale: Int): Unit =
    g.setColor(SimulationColor.VISIBILITY_RANGE_COLOR)
    g.fillOval(
      scaleToView(structure.position.x - structure.visibilityDistance.toInt, scale),
      scaleToView(structure.position.y - structure.visibilityDistance.toInt, scale),
      scaleToView(structure.visibilityDistance.toInt * 2 + 1, scale),
      scaleToView(structure.visibilityDistance.toInt * 2 + 1, scale)
    )

  private def drawEntities(g: Graphics2D, environment: Environment, scale: Int): Unit =
    for entity <- environment.entities do
      entity match
        case entity: BaseEntity =>
          g.setColor(
            SimulationColor.ageColor(
              if entity.infection.isDefined then SimulationColor.INFECTED_ENTITY_COLOR
              else SimulationColor.HEALTHY_ENTITY_COLOR,
              entity.age
            )
          )
          g.fillOval(scaleToView(entity.position.x, scale), scaleToView(entity.position.y, scale), scale, scale)
          g.setColor(if entity.immunity > 0 then SimulationColor.IMMUNITY_COLOR else Color.WHITE)
          g.drawOval(scaleToView(entity.position.x, scale), scaleToView(entity.position.y, scale), scale, scale)

  private def scaleToView(coordinate: Int, scale: Int): Int = coordinate * scale

  override def init(): Task[Unit] = io(setOpaque(true))

  override def update(newEnv: Environment): Task[Unit] = for
    _ <- io { env = Some(newEnv) }
    _ <- io(this.repaint())
  yield ()

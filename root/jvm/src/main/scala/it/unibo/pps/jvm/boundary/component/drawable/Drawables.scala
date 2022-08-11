package it.unibo.pps.jvm.boundary.component.drawable

import it.unibo.pps.boundary.component.drawable.Drawable
import it.unibo.pps.entity.entity.Entities.{BaseEntity, SimulationEntity}
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Visible
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import it.unibo.pps.jvm.boundary.Values.SimulationColor

import java.awt.geom.AffineTransform
import java.awt.{Color, Font}
import scala.swing.Graphics2D

/** Define given instances for drawable types */
object Drawables:
  import Utils.scaleToView

  /** Extend [[Environment]] with draw capabilities */
  given Drawable[Environment] with
    import it.unibo.pps.boundary.component.drawable.DrawableOps.*
    extension (env: Environment)
      def draw(g: Graphics2D, scale: Int): Unit =
        env.structures.drawAll(g, scale)
        env.entities.drawAll(g, scale)

  /** Extend [[SimulationEntity]] with draw capabilities */
  given Drawable[SimulationEntity] with
    extension (elem: SimulationEntity)
      def draw(g: Graphics2D, scale: Int): Unit =
        elem match
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

  /** Extend [[SimulationStructure]] with draw capabilities */
  given Drawable[SimulationStructure] with
    extension (structure: SimulationStructure)
      def draw(g: Graphics2D, scale: Int): Unit =
        structure match
          case house: House =>
            g.setColor(SimulationColor.HOUSE_COLOR)
          case gen: GenericBuilding =>
            drawStructureVisibility(g, gen, scale)
            g.setColor(if gen.isOpen then SimulationColor.GENERIC_COLOR_OPEN else SimulationColor.GENERIC_COLOR_CLOSED)
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

  private object Utils:
    def scaleToView(coordinate: Int, scale: Int): Int = coordinate * scale

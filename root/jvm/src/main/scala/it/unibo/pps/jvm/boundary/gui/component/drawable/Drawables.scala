package it.unibo.pps.jvm.boundary.gui.component.drawable

import it.unibo.pps.boundary.ViewUtils.scaleToView
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Visible
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import it.unibo.pps.jvm.boundary.gui.Values.{Dimension, SimulationColor}
import DrawableConcept.DrawableSwing

import java.awt.geom.AffineTransform
import java.awt.{Color, Font, Graphics2D}

/** Define given instances for drawable types. */
object Drawables:

  /** Extend [[Environment]] with jvm-swing draw capabilities. */
  given DrawableSwing[Environment] with
    import DrawableConcept.DrawableOps.*
    extension (env: Environment)
      def draw(g: Graphics2D, scale: Int): Unit =
        env.structures.drawAll(g, scale)
        env.externalEntities.drawAll(g, scale)

  /** Extend [[SimulationEntity]] with jvm-swing draw capabilities. */
  given DrawableSwing[SimulationEntity] with
    extension (entity: SimulationEntity)
      def draw(g: Graphics2D, scale: Int): Unit =
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

  /** Extend [[SimulationStructure]] with jvm-swing draw capabilities. */
  given DrawableSwing[SimulationStructure] with
    extension (structure: SimulationStructure)
      def draw(g: Graphics2D, scale: Int): Unit =
        structure match
          case house: House =>
            g.setColor(SimulationColor.HOUSE_COLOR)
          case gen: GenericBuilding =>
            if gen.isOpen then drawStructureVisibility(g, gen, scale)
            drawRemainedCapacity(g, structure, scale)
            g.setColor(if gen.isOpen then SimulationColor.GENERIC_COLOR_OPEN else SimulationColor.GENERIC_COLOR_CLOSED)
          case hospital: Hospital =>
            drawStructureVisibility(g, hospital, scale)
            drawRemainedCapacity(g, structure, scale)
            g.setColor(SimulationColor.HOSPITAL_COLOR)
        g.fillRect(scaleToView(structure.position.x, scale), scaleToView(structure.position.y, scale), scale, scale)

    private def drawRemainedCapacity(g: Graphics2D, structure: SimulationStructure, scale: Int): Unit =
      val message = s"${structure.capacity - structure.entities.size}"
      val font = Font(Font.SANS_SERIF, Font.BOLD, Dimension.FONT_DIMENSION)
      val scaleFont = AffineTransform()
      val fontMetrics = g.getFontMetrics(font)
      val scaleF = Math.min(
        scale.toDouble / (fontMetrics.getMaxAscent + fontMetrics.getMaxDescent),
        scale.toDouble / fontMetrics.stringWidth(message)
      )
      scaleFont.scale(scaleF, scaleF)
      g.setFont(font.deriveFont(scaleFont))
      g.setColor(SimulationColor.STRUCTURE_CAPACITY_COLOR)
      g.drawString(
        message,
        scaleToView(structure.position.x, scale),
        scaleToView(structure.position.y, scale)
      )

    private def drawStructureVisibility(g: Graphics2D, structure: SimulationStructure with Visible, scale: Int): Unit =
      g.setColor(SimulationColor.VISIBILITY_RANGE_COLOR)
      g.fillOval(
        scaleToView(structure.position.x - structure.visibilityDistance.toInt, scale),
        scaleToView(structure.position.y - structure.visibilityDistance.toInt, scale),
        scaleToView(structure.visibilityDistance.toInt * 2 + 1, scale),
        scaleToView(structure.visibilityDistance.toInt * 2 + 1, scale)
      )

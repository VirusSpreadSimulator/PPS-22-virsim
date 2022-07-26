package it.unibo.pps.js.boundary.gui.component.drawable

import it.unibo.pps.boundary.ViewUtils.scaleToView
import it.unibo.pps.entity.entity.Entities.SimulationEntity
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.entity.structure.StructureComponent.Visible
import it.unibo.pps.entity.structure.Structures.{GenericBuilding, Hospital, House, SimulationStructure}
import it.unibo.pps.js.boundary.gui.Values.SimulationColor
import it.unibo.pps.js.boundary.gui.component.drawable.DrawableConcept.DrawableJS
import org.scalajs.dom

/** Define given instances for drawable types. */
object Drawables:

  /** Extend [[Environment]] with js draw capabilities. */
  given DrawableJS[Environment] with
    import it.unibo.pps.js.boundary.gui.component.drawable.DrawableConcept.DrawableOps.*
    extension (env: Environment)
      def draw(g: dom.CanvasRenderingContext2D, scale: Int): Unit =
        env.structures.drawAll(g, scale)
        env.externalEntities.drawAll(g, scale)

  /** Extend [[SimulationEntity]] with js draw capabilities. */
  given DrawableJS[SimulationEntity] with
    extension (entity: SimulationEntity)
      def draw(g: dom.CanvasRenderingContext2D, scale: Int): Unit =
        g.fillStyle =
          if entity.infection.isDefined then SimulationColor.INFECTED_ENTITY_COLOR
          else SimulationColor.HEALTHY_ENTITY_COLOR
        g.beginPath()
        g.arc(
          scaleToView(entity.position.x, scale) + scale / 2,
          scaleToView(entity.position.y, scale) + scale / 2,
          scale / 2 - 1,
          0,
          2 * Math.PI
        )
        g.fill()
        g.strokeStyle = if entity.immunity > 0 then SimulationColor.IMMUNITY_COLOR else SimulationColor.WHITE
        g.stroke()

  /** Extend [[SimulationStructure]] with js draw capabilities. */
  given DrawableJS[SimulationStructure] with
    extension (structure: SimulationStructure)
      def draw(g: dom.CanvasRenderingContext2D, scale: Int): Unit =
        structure match
          case house: House =>
            g.fillStyle = SimulationColor.HOUSE_COLOR
          case gen: GenericBuilding =>
            if gen.isOpen then drawStructureVisibility(g, gen, scale)
            drawRemainedCapacity(g, structure, scale)
            g.fillStyle =
              if gen.isOpen then SimulationColor.GENERIC_COLOR_OPEN else SimulationColor.GENERIC_COLOR_CLOSED
          case hospital: Hospital =>
            drawStructureVisibility(g, hospital, scale)
            drawRemainedCapacity(g, structure, scale)
            g.fillStyle = SimulationColor.HOSPITAL_COLOR
        g.fillRect(scaleToView(structure.position.x, scale), scaleToView(structure.position.y, scale), scale, scale)

    private def drawRemainedCapacity(
        g: dom.CanvasRenderingContext2D,
        structure: SimulationStructure,
        scale: Int
    ): Unit =
      g.font = s"${scale / 1.5}px sans-serif"
      g.fillStyle = SimulationColor.STRUCTURE_CAPACITY_COLOR
      g.fillText(
        s"${structure.capacity - structure.entities.size}",
        scaleToView(structure.position.x, scale),
        scaleToView(structure.position.y, scale)
      )

    private def drawStructureVisibility(
        g: dom.CanvasRenderingContext2D,
        structure: SimulationStructure with Visible,
        scale: Int
    ): Unit =
      g.beginPath()
      g.fillStyle = SimulationColor.VISIBILITY_RANGE_COLOR
      g.arc(
        scaleToView(structure.position.x, scale) + scale / 2,
        scaleToView(structure.position.y, scale) + scale / 2,
        scaleToView(structure.visibilityDistance.toInt, scale) + scale / 2,
        0,
        2 * Math.PI
      )
      g.fill()
      g.strokeStyle = SimulationColor.VISIBILITY_RANGE_COLOR
      g.stroke()

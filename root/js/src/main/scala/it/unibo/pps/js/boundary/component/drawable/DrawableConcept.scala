package it.unibo.pps.js.boundary.component.drawable

import it.unibo.pps.boundary.component.drawable.Drawable
import org.scalajs.dom

object DrawableConcept:
  /** JS-based [[Drawable]] type class for extending a type with draw capabilities
    * @tparam A
    *   the type to extend
    */
  trait DrawableJS[A] extends Drawable[A]:
    override type Graphic = dom.CanvasRenderingContext2D

  /** A set of operations modeled on the JS-based Drawable type-class */
  object DrawableOps:
    /** Method to draw all the elements in a set
      * @param g
      *   : the graphics needed to draw
      * @param scale
      *   : the scale factor to apply
      */
    extension [A: DrawableJS](drawables: Set[A])
      def drawAll(g: dom.CanvasRenderingContext2D, scale: Int): Unit =
        for drawable <- drawables do drawable.draw(g, scale)

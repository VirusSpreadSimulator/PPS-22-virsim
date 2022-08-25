package it.unibo.pps.jvm.boundary.gui.component.drawable

import it.unibo.pps.boundary.component.drawable.Drawable
import java.awt.Graphics2D

object DrawableConcept:
  /** JVM-based [[Drawable]] type class for extending a type with draw capabilities
    * @tparam A
    *   the type to extend
    */
  trait DrawableSwing[A] extends Drawable[A]:
    override type Graphic = Graphics2D

  /** A set of operations modeled on the JVM-based Drawable type-class */
  object DrawableOps:
    extension [A: DrawableSwing](drawables: Set[A])
      /** Method to draw all the elements in a set
        * @param g
        *   : the graphics needed to draw
        * @param scale
        *   : the scale factor to apply
        */
      def drawAll(g: Graphics2D, scale: Int): Unit = for drawable <- drawables do drawable.draw(g, scale)

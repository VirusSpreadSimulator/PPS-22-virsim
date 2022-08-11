package it.unibo.pps.boundary.component.drawable

import java.awt.Graphics2D

/** Type class for extending a type with draw capabilities
  * @tparam A
  *   the type to extend
  */
trait Drawable[A]:
  /** Method to draw the element
    * @param g:
    *   the graphics needed to draw
    * @param scale:
    *   the scale factor to apply
    */
  extension (elem: A) def draw(g: Graphics2D, scale: Int): Unit

/** A set of operations modeled on the Drawable type-class */
object DrawableOps:
  /** Method to draw all the elements in a set
    * @param g
    *   : the graphics needed to draw
    * @param scale
    *   : the scale factor to apply
    */
  extension [A: Drawable](drawables: Set[A])
    def drawAll(g: Graphics2D, scale: Int): Unit = for drawable <- drawables do drawable.draw(g, scale)

package it.unibo.pps.boundary.component.drawable

/** Base type class for extending a type with draw capabilities.
  * @tparam A
  *   the type to extend
  */
trait Drawable[A]:
  /** The graphic type used to draw. */
  type Graphic
  /** Method to draw the element.
    * @param g:
    *   the graphics needed to draw
    * @param scale:
    *   the scale factor to apply
    */
  extension (elem: A) def draw(g: Graphic, scale: Int): Unit

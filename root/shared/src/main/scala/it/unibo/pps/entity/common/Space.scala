package it.unibo.pps.entity.common

import scala.annotation.targetName

object Space:
  /** Alias for modeling a Distance in a Space in a more declarative way. */
  type Distance = Double
  /** Model a Point in a 2D Space
    * @param x
    *   the x coordinate
    * @param y
    *   the Y coordinate
    */
  case class Point2D(x: Long, y: Long)

  object Point2D:
    given Conversion[(Long, Long), Point2D] with
      def apply(p: (Long, Long)): Point2D = Point2D(p._1, p._2)

  extension (p: Point2D)
    /** General method to combine two [[Point2D]]
      * @param other
      *   the other point to combine with
      * @param f
      *   the function applied to both the coordinate of the point
      */
    def combine(other: Point2D)(f: (Long, Long) => Long): Point2D = Point2D(f(p.x, other.x), f(p.y, other.y))
    /** Plus operator
      *
      * @param other
      *   the other point to sum with
      * @return
      */
    @targetName("plus")
    def +(other: Point2D): Point2D = p.combine(other)(_ + _)
    /** Minus operator
      *
      * @param other
      *   the other point involved in the operation
      * @return
      */
    @targetName("minus")
    def -(other: Point2D): Point2D = p.combine(other)(_ - _)
    /** Product operator
      * @param other
      *   the other point involved in the operation
      * @return
      */
    @targetName("product")
    def *(other: Point2D): Point2D = p.combine(other)(_ * _)
    /** Get the distance between the current and the provided point
      * @param other
      *   the point to witch compute the distance
      * @return
      */
    def distanceTo(other: Point2D): Distance = Math.hypot(other.x - p.x, other.y - p.y)

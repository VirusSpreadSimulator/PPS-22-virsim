package it.unibo.pps.entity.common

import scala.reflect.ClassTag

/** Module that contains some utils useful with types and collections. */
object Utils:
  extension [A: ClassTag](set: Set[A])
    /** Select the elements from the set that are of the specified type T. */
    def select[T: ClassTag]: Set[T] = set.collect { case elem: T => elem }
  extension [A: ClassTag](elem: A)
    /** Try to obtain the element with the specified T capabilities. It returns an [[Option]] */
    def withCapabilities[T: ClassTag]: Option[T] = elem match
      case elem: T => Some(elem)
      case _ => None
    /** Express a computation to perform on element only if a condition on that element is satisfied.
      * @param f
      *   the condition on the element
      * @param update
      *   the function to apply if [[f]] is true
      */
    def andIf(f: A => Boolean)(update: A => A): A =
      if f(elem) then update(elem) else elem

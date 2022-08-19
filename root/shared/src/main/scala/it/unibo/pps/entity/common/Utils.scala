package it.unibo.pps.entity.common

import scala.reflect.ClassTag

object Utils:
  extension [A: ClassTag](set: Set[A]) def select[T: ClassTag]: Set[T] = set.collect { case elem: T => elem }
  extension [A: ClassTag](elem: A)
    def withCapabilities[T: ClassTag]: Option[T] = elem match
      case elem: T => Some(elem)
      case _ => None
  extension [A](e: A)
    def andIf(f: A => Boolean)(update: A => A): A =
      if f(e) then update(e) else e

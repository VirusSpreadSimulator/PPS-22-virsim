package it.unibo.pps.entity.common

import scala.reflect.ClassTag

object Utils:
  extension [A: ClassTag](set: Set[A]) def select[T: ClassTag]: Set[T] = set.collect { case elem: T => elem }

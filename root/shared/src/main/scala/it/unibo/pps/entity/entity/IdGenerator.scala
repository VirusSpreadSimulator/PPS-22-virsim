package it.unibo.pps.entity.entity
import monocle.syntax.all.*

object IdGenerator:
  trait Generator[A]:
    def current: A
    def next(): A
    def increment(): Generator[A]

  case class IntegerIdGenerator(override val current: Int) extends Generator[Int]:
    override def next(): Int = increment().current
    override def increment(): Generator[Int] = this.focus(_.current).modify(_ + 1)

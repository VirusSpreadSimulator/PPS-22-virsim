package it.unibo.pps.common

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.common.GaussianProperty.Gaussian

class GaussianPropertyTests extends AnyFunSuite with Matchers:
  private case class GaussianExample(mean: Double, std: Double) extends Gaussian[Double]:
    override def next(): Double = nextGaussian()
  private val tolerance = 1e-0

  test("A gaussian property must respect the mean for a thousands of elements with a tolerance of +-1") {
    val mean = 10
    val std = 2
    val g = GaussianExample(mean, std)
    val elems: Seq[Double] = for
      _ <- 0 to 1000
      next = g.next()
    yield next
    (elems.sum / elems.size) shouldBe (mean.toDouble +- tolerance)
  }

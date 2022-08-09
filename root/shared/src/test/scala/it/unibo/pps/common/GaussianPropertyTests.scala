package it.unibo.pps.common

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.entity.common.GaussianProperty.Gaussian

class GaussianPropertyTests extends AnyFunSuite with Matchers:
  private case class GaussianExample(mean: Double, std: Double) extends Gaussian[Double]:
    override def next(): Double = nextGaussian()
  private val tolerance = 1e-0
  private val mean = 10
  private val std = 2
  private val g = GaussianExample(mean, std)

  test("A gaussian property must allow to obtain the mean") {
    g.mean shouldBe mean
  }

  test("A gaussian property must allow to obtain the standard deviation") {
    g.std shouldBe std
  }

  test("A gaussian property must respect the mean for a thousands of elements with a tolerance of +-1") {
    val elems: Seq[Double] = for
      _ <- 0 to 1000
      next = g.next()
    yield next
    (elems.sum / elems.size) shouldBe (mean.toDouble +- tolerance)
  }

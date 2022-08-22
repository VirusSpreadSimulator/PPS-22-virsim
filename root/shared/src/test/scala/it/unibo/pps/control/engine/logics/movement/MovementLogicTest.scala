package it.unibo.pps.control.engine.logics.movement

import it.unibo.pps.entity.common.Space.Point2D
import it.unibo.pps.prolog.PrologNextMovement
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MovementLogicTest extends AnyFunSuite with Matchers:
  val worldWidth = 50
  val worldHeight = 50
  val step = 1

//  test("an entity in the middle of the world can move everywhere") {
//    val xCoord = worldWidth / 2
//    val yCoord = worldHeight / 2
//    PrologNextMovement.calculateNextMovement(Point2D(xCoord, yCoord), worldWidth, worldHeight, step) shouldBe Set(
//      Point2D(xCoord - step, yCoord - step),
//      Point2D(xCoord - step, yCoord),
//      Point2D(xCoord - step, yCoord + step),
//      Point2D(xCoord, yCoord - step),
//      Point2D(xCoord, yCoord + step),
//      Point2D(xCoord + step, yCoord - step),
//      Point2D(xCoord + step, yCoord),
//      Point2D(xCoord + step, yCoord + step)
//    )
//  }
//
//  test("An entity with x-coordinate = 0 can't move to the left") {
//    val xCoord = 0
//    val yCoord = 10
//    PrologNextMovement.calculateNextMovement(Point2D(xCoord, yCoord), worldWidth, worldHeight, step) shouldBe Set(
//      Point2D(xCoord, yCoord - step),
//      Point2D(xCoord, yCoord + step),
//      Point2D(xCoord + step, yCoord - step),
//      Point2D(xCoord + step, yCoord),
//      Point2D(xCoord + step, yCoord + step)
//    )
//  }
//
//  test("an entity with x-coordinate = worldWidth can't move to the right") {
//    val xCoord = worldWidth
//    val yCoord = 10
//    PrologNextMovement.calculateNextMovement(Point2D(xCoord, yCoord), worldWidth, worldHeight, step) shouldBe Set(
//      Point2D(xCoord - step, yCoord - step),
//      Point2D(xCoord - step, yCoord),
//      Point2D(xCoord - step, yCoord + step),
//      Point2D(xCoord, yCoord - step),
//      Point2D(xCoord, yCoord + step)
//    )
//  }
//
//  test("an entity with y-coordinate = 0 can't move up") {
//    val xCoord = 10
//    val yCoord = 0
//    PrologNextMovement.calculateNextMovement(Point2D(xCoord, yCoord), worldWidth, worldHeight, step) shouldBe Set(
//      Point2D(xCoord - step, yCoord),
//      Point2D(xCoord - step, yCoord + step),
//      Point2D(xCoord, yCoord + step),
//      Point2D(xCoord + step, yCoord),
//      Point2D(xCoord + step, yCoord + step)
//    )
//  }
//
//  test("an entity with y-coordinate = worldHeight can't move down") {
//    val xCoord = 10
//    val yCoord = worldHeight
//    PrologNextMovement.calculateNextMovement(Point2D(xCoord, yCoord), worldWidth, worldHeight, step) shouldBe Set(
//      Point2D(xCoord - step, yCoord - step),
//      Point2D(xCoord - step, yCoord),
//      Point2D(xCoord, yCoord - step),
//      Point2D(xCoord + step, yCoord - step),
//      Point2D(xCoord + step, yCoord)
//    )
//  }
//
//  test("an entity with backToHome goal comes closer to home with every movement") {
//    val homePosition = Point2D(25, 50)
//    val xCoord = worldWidth / 2
//    val yCoord = worldHeight / 2
//    PrologNextMovement.calculateNextMovementToGoHome(
//      Point2D(xCoord, yCoord),
//      worldWidth,
//      worldHeight,
//      step,
//      homePosition
//    ) shouldBe Set(
//      Point2D(xCoord - step, yCoord + step),
//      Point2D(xCoord, yCoord + step),
//      Point2D(xCoord + step, yCoord + step)
//    )
//  }

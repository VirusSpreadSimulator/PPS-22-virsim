package it.unibo.pps.prolog

import PrologEngine.{*, given}
import alice.tuprolog.{Prolog, SolveInfo, Term, Theory, Struct, Var}
import it.unibo.pps.entity.common.Space.Point2D

object PrologNextMovement:
  val newX: Var = Var("Xn")
  val newY: Var = Var("Yn")

  /** Calculates all possible next movement based on current position
    * @param currPosition
    *   the current position of the entity
    * @param worldWidth
    *   the width of the world
    * @param worldHeight
    *   the height of the world
    * @param step
    *   the step of the movement
    * @return
    *   a set of possible next position
    */
  def calculateNextMovement(currPosition: Point2D, worldWidth: Int, worldHeight: Int, step: Int): Set[Point2D] =
    val currentPosition = s"point(${currPosition._1}, ${currPosition._2})"
    val input = Struct(
      "newPoint",
      Term.createTerm(currentPosition),
      newX,
      newY,
      -step to step,
      Term.createTerm(worldWidth.toString),
      Term.createTerm(worldHeight.toString)
    )
    engine(input)
      .map(a => Point2D(extractTerm(a, 1).toString.toInt, extractTerm(a, 2).toString.toInt))
      .toSet

  /** Calculates all possible next movement for entity that has to return to home
    * @param currPosition
    *   the current position of the entity
    * @param worldWidth
    *   the width of the world
    * @param worldHeight
    *   the height of the world
    * @param step
    *   the step of the movement
    * @param homePos
    *   the position of the home
    * @return
    *   a set of possible next position
    */
  def calculateNextMovementToGoHome(
      currPosition: Point2D,
      worldWidth: Int,
      worldHeight: Int,
      step: Int,
      homePos: Point2D
  ): Set[Point2D] =
    val currentPosition = s"point(${currPosition._1}, ${currPosition._2})"
    val homePosition = s"home(${homePos._1}, ${homePos._2})"
    val input = Struct(
      "goHome",
      Term.createTerm(currentPosition),
      newX,
      newY,
      -step to step,
      Term.createTerm(worldWidth.toString),
      Term.createTerm(worldHeight.toString),
      Term.createTerm(homePosition)
    )
    engine(input)
      .map(a => Point2D(extractTerm(a, 1).toString.toInt, extractTerm(a, 2).toString.toInt))
      .toSet

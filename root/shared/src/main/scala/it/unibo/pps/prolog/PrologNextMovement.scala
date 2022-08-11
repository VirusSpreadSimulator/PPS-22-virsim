package it.unibo.pps.prolog

import PrologEngine.{*, given}
import alice.tuprolog.{Prolog, SolveInfo, Term, Theory, Struct, Var}
import it.unibo.pps.entity.common.Space.Point2D

object PrologNextMovement:

  /** The logic to move the entity */
  val logic: Term => LazyList[Term] = mkPrologEngine("""
    changeX(X, List, Xn, Width) :- member(N, List), Xn is X + N, Xn >= 0, Xn =< Width.
    changeY(Y, List, Yn, Height) :- member(N, List), Yn is Y + N, Yn >= 0, Yn =< Height.

    newPoint(point(X, Y), Xn, Yn, List, Width, Height) :- changeX(X, List, Xn, Width), changeY(Y, List, Yn, Height),  changed(X, Xn, Y, Yn).

    changed(X, Xn, Y, Yn) :- X =\= Xn, !.
    changed(X, Xn, Y, Yn) :- Y =\= Yn, !.

    goHome(point(X, Y), Xn, Yn, List, Width, Height, home(Xh, Yh)) :- newPoint(point(X, Y), Xn, Yn, List, Width, Height), closer(point(X,Y), point(Xn, Yn), point(Xh, Yh)).

    closer(point(X,Y), point(Xn, Yn), point(Xh, Yh)) :- distance(point(X, Y), point(Xh, Yh), CurrDist), distance(point(Xn, Yn), point(Xh, Yh), NewDist), NewDist < CurrDist.

    distance(point(X,Y), point(X2,Y2), Dist):- DeltaX is X2-X, DeltaY is Y2-Y, pow(DeltaX,2,PowX), pow(DeltaY,2,PowY), Dist is sqrt(PowX+PowY).

    pow(X, Esp, Y):-pow(X, X, Esp, Y).
    pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
    pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
    pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).
  """)

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
    logic(input)
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
    * @param homePosition
    *   the position of the home
    * @return
    *   a set of possible next position
    */
  def calculateNextMovementToGoHome(
      currPosition: Point2D,
      worldWidth: Int,
      worldHeight: Int,
      step: Int,
      homePosition: Point2D
  ): Set[Point2D] =
    val currentPosition = s"point(${currPosition._1}, ${currPosition._2})"
    val homePosition = s"home(${homePosition._1}, ${homePosition._2})"
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
    logic(input)
      .map(a => Point2D(extractTerm(a, 1).toString.toInt, extractTerm(a, 2).toString.toInt))
      .toSet

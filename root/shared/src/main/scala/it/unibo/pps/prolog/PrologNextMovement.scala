package it.unibo.pps.prolog

import PrologEngine.{*, given}
import alice.tuprolog.{Prolog, SolveInfo, Term, Theory, Struct, Var}
import it.unibo.pps.entity.common.Space.Point2D

object PrologNextMovement:

  val logicRandomMovement: Term => LazyList[Term] = mkPrologEngine("""
    changeX(X, List, Xn, Width) :- member(N, List), Xn is X + N, Xn >= 0, Xn =< Width.
    changeY(Y, List, Yn, Height) :- member(N, List), Yn is Y + N, Yn >= 0, Yn =< Height.
    newPoint(point(X, Y), Xn, Yn, List, Width, Height) :- changeX(X, List, Xn, Width), changeY(Y, List, Yn, Height),  changed(X, Xn, Y, Yn).
    changed(X, Xn, Y, Yn) :- X =\= Xn, !. 
    changed(X, Xn, Y, Yn) :- Y =\= Yn, !. 
  """)

  val newX: Var = Var("Xn")
  val newY: Var = Var("Yn")

  def calculateNextMovement(currentPosition: Point2D, worldWidth: Int, worldHeight: Int, maxSteps: Int): Set[Point2D] =
    val point = s"point(${currentPosition._1}, ${currentPosition._2})"
    val input = Struct(
      "newPoint",
      Term.createTerm(point),
      newX,
      newY,
      -maxSteps to maxSteps,
      Term.createTerm(worldWidth.toString),
      Term.createTerm(worldHeight.toString)
    )
    logicRandomMovement(input)
      .map(a => Point2D(extractTerm(a, 1).toString.toInt, extractTerm(a, 2).toString.toInt))
      .toSet

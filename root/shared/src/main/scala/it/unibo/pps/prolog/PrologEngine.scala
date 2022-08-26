package it.unibo.pps.prolog

import alice.tuprolog.{Prolog, SolveInfo, Term, Theory, Struct}

object PrologEngine:
  given Conversion[String, Term] = Term.createTerm(_)
  given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")

  private val engine = new Prolog()
  engine.setTheory(new Theory(getClass.getResource("/prologTheory.pl").openStream()))

  def extractTerm(t: Term, i: Integer): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm
  def engine(goal: Term): LazyList[Term] =
    new Iterable[Term] {
      override def iterator: Iterator[Term] = new Iterator[Term] {
        var solution: SolveInfo = engine.solve(goal)
        override def hasNext: Boolean =
          solution.isSuccess || solution.hasOpenAlternatives
        override def next(): Term =
          try solution.getSolution
          finally solution = engine.solveNext
      }
    }.to(LazyList)

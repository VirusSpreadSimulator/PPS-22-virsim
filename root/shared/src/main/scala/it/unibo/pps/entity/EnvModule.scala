package it.unibo.pps.entity

object EnvModule:
  trait Env:
    def getState(): State
    def updateState(state: State): Unit
  trait Provider:
    val env: Env
  trait Component:
    class EnvImpl extends Env:
      private var state: State = State(0)

      override def getState() = state
      override def updateState(newState: State): Unit =
        state = newState
  trait Interface extends Provider with Component

package it.unibo.pps.entity.Entity

object EntityComponent {
  trait Entity:
    type Home
    type Position

    def age: Int
    def home: Home
    def position: Position //could be a position in the grid or a structure
    def health: Int
    def maxHealth: Int
    def immunity: Int
    def infection: Option[Infection]

    case class Infection(severity: Int, infectionDuration: Int)
}

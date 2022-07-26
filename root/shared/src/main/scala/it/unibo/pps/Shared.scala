package it.unibo.pps

import scala.concurrent.{Await, Future}

final case class Person(name: String, surname: String)
// Application specific
trait UI:
  def render(person: Person): Unit

def application(ui: UI): Unit =
  val me = Person("Gianluca", "Aguzzi")
  println("Shared part...")
  ui.render(me)

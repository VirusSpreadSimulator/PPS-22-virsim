package it.unibo.pps.jvm

import it.unibo.pps.{Person, UI, application}

import scala.swing.*

def render: UI = (person: Person) =>
  new Frame {
    title = s"Hello"
    size = new Dimension(600, 100)
    contents = new FlowPanel {
      contents += Label(s"${person.surname} ${person.name} this is a JVM App!!")
    }
    centerOnScreen()
    pack()
    open()
  }

@main def main(): Unit = application(render)

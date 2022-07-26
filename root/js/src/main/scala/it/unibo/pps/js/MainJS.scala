package it.unibo.pps.js

import it.unibo.pps.{UI, application}

import scala.scalajs.js
import org.scalajs.dom
import org.scalajs.dom.html.Image

def render: UI = person =>
  val image = dom.document.createElement("img").asInstanceOf[Image]
  image.src =
    "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Scala-full-color.svg/1200px-Scala-full-color.svg.png"
  dom.document.body.appendChild(image)
  dom.window.alert(s"Hi ${person.name} from Scala")

// in sbt shell, type fastOptJS
// open index.html
@main def main(): Unit =
  application(render)

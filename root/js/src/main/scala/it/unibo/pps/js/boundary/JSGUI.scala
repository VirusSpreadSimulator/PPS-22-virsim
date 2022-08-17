package it.unibo.pps.js.boundary

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Image}
import it.unibo.pps.boundary.ViewUtils.*
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.js.boundary.component.MonadButton

trait JSGUI:
  def init(): Task[Unit]
  def config(): Task[String]
  def error(errors: Seq[ConfigurationError]): Task[Unit]
  def render(i: Int): Task[Unit]
  def events(): Observable[Event]

object JSGUI:
  def apply(title: String = "Virsim"): JSGUI = JSGUIImpl(title)
  private class JSGUIImpl(title: String) extends JSGUI:
    private lazy val renderBtns: Seq[MonadButton] =
      Seq(
        MonadButton("Click me!", Pause)
      )

    override def init(): Task[Unit] =
      for _ <- io(renderBtns.foreach(btn => dom.document.getElementById("div-center").appendChild(btn.button)))
      yield ()

    override def render(i: Int): Task[Unit] = Task(dom.console.log(i))

    override def events(): Observable[Event] = Observable
      .fromIterable(renderBtns)
      .flatMap(_.events)

    override def config(): Task[String] = ???

    override def error(errors: Seq[ConfigurationError]): Task[Unit] = ???
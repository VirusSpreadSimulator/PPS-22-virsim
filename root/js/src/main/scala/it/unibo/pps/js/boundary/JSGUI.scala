package it.unibo.pps.js.boundary

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.boundary.component.Events.Event.*
import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.{Consumer, Observable, OverflowStrategy}
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Image}
import it.unibo.pps.boundary.ViewUtils.*
import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.entity.environment.EnvironmentModule.Environment
import it.unibo.pps.js.boundary.component.MonadComponents.*
import monix.reactive.subjects.PublishSubject

trait JSGUI:
  def init(): Task[Unit]
  def config(): Task[dom.File]
  def error(errors: Seq[ConfigurationError]): Task[Unit]
  def start(): Task[Unit]
  def stop(): Task[Unit]
  def consume(env: Environment): Task[Unit]
  def events(): Observable[Event]

object JSGUI:
  def apply(title: String = "Virsim"): JSGUI = JSGUIImpl(title)
  private class JSGUIImpl(title: String) extends JSGUI:
    private lazy val fileChosen = PublishSubject[dom.File]()

    override def init(): Task[Unit] =
      for
        fileInput <- io(dom.document.getElementById("file_input").asInstanceOf[dom.html.Input])
        _ <- io(fileInput.onchange = e => fileChosen.onNext(fileInput.files(0)))
      yield ()

    override def config(): Task[dom.File] = Task.defer(fileChosen.consumeWith(Consumer.head))

    override def error(errors: Seq[ConfigurationError]): Task[Unit] = ???

    override def start(): Task[Unit] = Task.pure {}

    override def stop(): Task[Unit] = Task.pure {}

    override def consume(env: Environment): Task[Unit] = Task(dom.console.log(env))

    override def events(): Observable[Event] = Observable
      .fromIterable(Seq[EventSource]())
      .flatMap(_.events)

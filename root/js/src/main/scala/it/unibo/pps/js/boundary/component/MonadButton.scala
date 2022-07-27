package it.unibo.pps.js.boundary.component

import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom
import org.scalajs.dom.html.Button

import java.awt.event.ActionEvent

trait MonadButton extends EventSource:
  def button: Button

object MonadButton:
  def apply(title: String, event: Event): MonadButton =
    val button = dom.document.createElement("button").asInstanceOf[Button]
    button.textContent = title
    MonadButtonImpl(button, event)
  private class MonadButtonImpl(override val button: Button, event: Event) extends MonadButton:
    override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
      button.addEventListener("click", (e: dom.MouseEvent) => out.onNext(event))
      Cancelable.empty
    }

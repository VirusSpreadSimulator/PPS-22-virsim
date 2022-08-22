package it.unibo.pps.js.boundary.component

import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom
import org.scalajs.dom.html.Button

import java.awt.event.ActionEvent

object MonadComponents:
  trait MonadButton extends EventSource:
    def button: Button
  object MonadButton:
    def apply(
        title: String,
        event: Event,
        cssClass: String = "btn",
        logicOnClick: (dom.MouseEvent, Button) => Unit = (_, _) => {}
    ): MonadButton =
      val button = dom.document.createElement("button").asInstanceOf[Button]
      button.textContent = title
      button.className = cssClass
      MonadButtonImpl(button, event, logicOnClick)
    private class MonadButtonImpl(
        override val button: Button,
        event: Event,
        logicOnClick: (dom.MouseEvent, Button) => Unit
    ) extends MonadButton:
      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        button.addEventListener(
          "click",
          (e: dom.MouseEvent) =>
            logicOnClick(e, button); out.onNext(event)
        )
        Cancelable.empty
      }

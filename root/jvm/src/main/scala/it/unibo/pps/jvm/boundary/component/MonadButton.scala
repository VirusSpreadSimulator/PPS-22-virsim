package it.unibo.pps.jvm.boundary.component

import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.jvm.boundary.component.MonadButton
import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}

import java.awt.event.ActionEvent
import javax.swing.JButton

trait MonadButton:
  def events: Observable[Event]
  def button: JButton

object MonadButton:
  def apply(title: String, event: Event): MonadButton =
    MonadButtonImpl(JButton(title), event)
  private class MonadButtonImpl(override val button: JButton, event: Event) extends MonadButton:
    override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
      button.addActionListener((e: ActionEvent) => out.onNext(event))
      Cancelable.empty
    }

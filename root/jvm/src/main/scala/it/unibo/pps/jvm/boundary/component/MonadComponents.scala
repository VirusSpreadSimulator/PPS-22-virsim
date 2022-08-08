package it.unibo.pps.jvm.boundary.component

import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import it.unibo.pps.boundary.ViewUtils.io

import javax.swing.{BoxLayout, JButton, JPanel, JTextField}
import java.awt.event.ActionEvent

object MonadComponents:
  trait MonadButton extends EventSource:
    def button: JButton
  object MonadButton:
    def apply(title: String, event: Event): MonadButton =
      MonadButtonImpl(JButton(title), event)
    private class MonadButtonImpl(override val button: JButton, event: Event) extends MonadButton:
      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        button.addActionListener((e: ActionEvent) => out.onNext(event))
        Cancelable.empty
      }

  trait MonadConfigButton extends EventSource:
    def button: JButton
    def textField: JTextField
    def panel: JPanel
  object MonadConfigButton:
    def apply(title: String, configCharLenght: Int, eventFactory: String => Event): MonadConfigButton =
      MonadConfigButtonImpl(JButton(title), JTextField(Math.min(10, configCharLenght)), eventFactory)
    private class MonadConfigButtonImpl(
        override val button: JButton,
        override val textField: JTextField,
        eventFactory: String => Event
    ) extends MonadConfigButton:
      override lazy val panel: JPanel =
        val p = JPanel()
        p.setLayout(BoxLayout(p, BoxLayout.X_AXIS))
        textField.setMaximumSize(textField.getPreferredSize);
        p.add(button)
        p.add(textField)
        p

      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        button.addActionListener((e: ActionEvent) => out.onNext(eventFactory(textField.getText)))
        Cancelable.empty
      }

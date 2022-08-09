package it.unibo.pps.jvm.boundary.component

import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import it.unibo.pps.boundary.ViewUtils.io

import javax.swing.{BoxLayout, JButton, JPanel, JTextField}
import java.awt.event.ActionEvent

/** Module that contains all the definition of custom components that can be easily integrated in a monadic system. */
object MonadComponents:
  /** MonadButton represent an [[EventSource]] that emit custom events when pressed. */
  trait MonadButton extends EventSource:
    /** @return the associated swing button. */
    def button: JButton
  object MonadButton:
    /** Factory to create a Monad Button
      * @param title
      *   the text to show in the button
      * @param event
      *   the associated event on the button click
      * @return
      *   the MonadButton
      */
    def apply(title: String, event: Event): MonadButton =
      MonadButtonImpl(JButton(title), event)
    private class MonadButtonImpl(override val button: JButton, event: Event) extends MonadButton:
      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        button.addActionListener((e: ActionEvent) => out.onNext(event))
        Cancelable.empty
      }

  /** MonadConfigButton represent an [[EventSource]] composed by a button and a text field. Usually it is useful to deal
    * with configuration button that need some text as input.
    */
  trait MonadConfigButton extends EventSource:
    /** @return the associated swing button */
    def button: JButton
    /** @return the associated swing text field */
    def textField: JTextField
    /** @return the panel that assemble the button and the text field horizontally. */
    def panel: JPanel
  object MonadConfigButton:
    /** Factory to create a MonadConfigButton
      * @param title
      *   the text to show in the button
      * @param configCharLenght
      *   the lenght of the textfield expressed in characters
      * @param eventFactory
      *   the factory that create the associated event from the string inserted by the user in the text field
      * @return
      *   the MonadConfigButton
      */
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

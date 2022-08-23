package it.unibo.pps.jvm.boundary.gui.component

import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import monix.eval.Task
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import it.unibo.pps.boundary.ViewUtils.io
import CustomSwingComponents.JNumericTextField
import it.unibo.pps.jvm.boundary.gui.Values.Margin
import javax.swing.{Box, BoxLayout, JButton, JComboBox, JPanel, JTextField}
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
    def apply(
        title: String,
        event: Event,
        customLogicOnClick: (ActionEvent, JButton) => Unit = (e, b) => {}
    ): MonadButton =
      MonadButtonImpl(JButton(title), event, customLogicOnClick)
    private class MonadButtonImpl(
        override val button: JButton,
        event: Event,
        customLogicOnClick: (ActionEvent, JButton) => Unit
    ) extends MonadButton:
      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        button.addActionListener { (e: ActionEvent) =>
          customLogicOnClick(e, button); out.onNext(event)
        }
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
    /** Factory to create a MonadConfigButton with a text-based field
      * @param title
      *   the text to show in the button
      * @param configLength
      *   the lenght of the textfield
      * @param eventFactory
      *   the factory that create the associated event from the string inserted by the user in the text field
      * @return
      *   the MonadConfigButton
      */
    def apply(title: String, configLength: Int, eventFactory: String => Event): MonadConfigButton =
      MonadConfigButtonImpl(JButton(title), JTextField(Math.min(10, configLength)), eventFactory)

    /** Factory to create a MonadConfigButton with a numeric-based field
      * @param title
      *   the text to show in the button
      * @param configLength
      *   the length of the textfield
      * @param eventFactory
      *   the factory that create the associated event from the string representation of the number inserted by the user
      *   in the field
      * @return
      */
    def numeric(
        title: String,
        configLength: Int,
        min: Int,
        max: Int,
        eventFactory: String => Event
    ): MonadConfigButton =
      MonadConfigButtonImpl(JButton(title), JNumericTextField(configLength, min, max), eventFactory)

    private class MonadConfigButtonImpl(
        override val button: JButton,
        override val textField: JTextField,
        eventFactory: String => Event
    ) extends MonadConfigButton:
      override lazy val panel: JPanel =
        val p = JPanel()
        p.setLayout(BoxLayout(p, BoxLayout.X_AXIS))
        textField.setMaximumSize(textField.getPreferredSize)
        p.add(button)
        p.add(Box.createRigidArea(Margin.DEFAULT_HMARGIN))
        p.add(textField)
        p

      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        button.addActionListener((e: ActionEvent) => out.onNext(eventFactory(textField.getText)))
        Cancelable.empty
      }

  trait MonadCombobox[A] extends EventSource:
    def combobox: JComboBox[A]
  object MonadCombobox:
    def apply[A](elems: Seq[A], selectedElem: A, eventFactory: A => Event): MonadCombobox[A] =
      val combobox = JComboBox[A]()
      for elem <- elems do combobox.addItem(elem)
      combobox.setSelectedItem(selectedElem)
      combobox.setMaximumSize(combobox.getPreferredSize)
      MonadComboboxImpl(combobox, eventFactory)
    private class MonadComboboxImpl[A](override val combobox: JComboBox[A], eventFactory: A => Event)
        extends MonadCombobox[A]:
      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        combobox.addActionListener((e: ActionEvent) =>
          out.onNext(eventFactory(combobox.getSelectedItem.asInstanceOf[A]))
        )
        Cancelable.empty
      }
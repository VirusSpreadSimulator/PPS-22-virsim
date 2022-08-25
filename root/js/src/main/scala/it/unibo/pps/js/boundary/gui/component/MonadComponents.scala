package it.unibo.pps.js.boundary.gui.component

import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.js.boundary.gui.Values.BootstrapClasses
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom
import org.scalajs.dom.{HTMLDivElement, HTMLElement, HTMLOptionElement}
import org.scalajs.dom.html.{Button, Input, Select}

/** Module that contains all the definition of custom components that can be easily integrated in a monadic system. */
object MonadComponents:
  import it.unibo.pps.js.boundary.gui.component.MonadComponents.Utils.{createButton, createInput}
  /** MonadButton represent an [[EventSource]] that emit custom events when pressed. */
  trait MonadButton extends EventSource:
    /** @return the associated html button. */
    def button: Button
  object MonadButton:
    /** Factory to create a Monad Button.
      * @param title
      *   the text to show in the button
      * @param event
      *   the associated event on the button click
      * @param cssClass
      *   the css class to style the button
      * @param logicOnClick
      *   the additional logic to perform on click
      * @return
      *   the MonadButton
      */
    def apply(
        title: String,
        event: Event,
        cssClass: String = BootstrapClasses.BTN,
        logicOnClick: (dom.MouseEvent, Button) => Unit = (_, _) => {}
    ): MonadButton =
      MonadButtonImpl(createButton(title, cssClass), event, logicOnClick)
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

  /** MonadSelect represent an [[EventSource]] that is composed by a select that emit events when the choice is changed.
    */
  trait MonadSelect extends EventSource:
    /** @return the associated html select */
    def select: Select
  object MonadSelect:
    /** Factory to create a Monad Select.
      * @param elems
      *   the elements to display in the select
      * @param selectedItem
      *   the selected item at the start
      * @param eventFactory
      *   the factory that create the associated event from the choice of the user
      * @tparam A
      *   the type of the elements
      * @return
      *   the MonadSelect
      */
    def apply[A](elems: Seq[A], selectedItem: A, eventFactory: A => Event): MonadSelect =
      val select = dom.document.createElement("select").asInstanceOf[Select]
      elems.foreach { elem =>
        val option = dom.document.createElement("option").asInstanceOf[HTMLOptionElement]
        option.text = elem.toString
        if elem == selectedItem then option.selected = true
        select.add(option)
      }
      MonadSelectImpl(select, elems, eventFactory)
    private class MonadSelectImpl[A](override val select: Select, elems: Seq[A], eventFactory: A => Event)
        extends MonadSelect:
      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        select.onchange = _ => out.onNext(eventFactory(elems(select.selectedIndex)))
        Cancelable.empty
      }

  /** MonadConfigButton represent an [[EventSource]] composed by a button and an input. Usually it is useful to deal
    * with configuration button that need some text as input.
    */
  trait MonadConfigButton extends EventSource:
    /** @return the associated html button. */
    def button: Button
    /** @return the associated html input. */
    def input: Input
    /** @return the div that assemble the button and the input horizontally. */
    def panel: HTMLElement
  object MonadConfigButton:
    /** Factory to create a MonadConfigButton with a text-based input.
      * @param title
      *   the text to show in the button
      * @param configLenght
      *   the length of the text input
      * @param eventFactory
      *   the factory that create the associated event from the string inserted by the user in the input
      * @return
      *   the MonadConfigButton
      */
    def apply(title: String, configLenght: Int, eventFactory: String => Event): MonadConfigButton =
      val button = createButton(title, BootstrapClasses.BTN_SECONDARY)
      val input = createInput("text", configLenght)
      MonadConfigButtonImpl(button, input, eventFactory)

    /** Factory to create a MonadConfigButton with a numeric-based input.
      * @param title
      *   the text to show in the button
      * @param lenght
      *   the length of the text input
      * @param min
      *   the minimum number that can be inserted by the user
      * @param max
      *   the maximum number that can be inserted by the user
      * @param factory
      *   the factory that create the associated event from the string representation of the number inserted by the user
      *   in the input
      * @return
      *   the MonadConfigButton
      */
    def numeric(title: String, lenght: Int, min: Int, max: Int, factory: String => Event): MonadConfigButton =
      val button = createButton(title, BootstrapClasses.BTN_SECONDARY)
      val input = createInput("number", lenght)
      input.min = min.toString
      input.max = max.toString
      input.step = "1"
      MonadConfigButtonImpl(button, input, factory)

    private class MonadConfigButtonImpl(
        override val button: Button,
        override val input: Input,
        eventFactory: String => Event
    ) extends MonadConfigButton:
      override lazy val panel: HTMLElement =
        val div = dom.document.createElement("div").asInstanceOf[HTMLDivElement]
        div.appendChild(button)
        div.appendChild(input)
        div.className = BootstrapClasses.MARGIN_TOP_1
        div

      override lazy val events: Observable[Event] = Observable.create(OverflowStrategy.Unbounded) { out =>
        button.addEventListener("click", _ => out.onNext(eventFactory(input.value)))
        Cancelable.empty
      }

  private object Utils:
    /** Create an html button
      * @param title
      *   the title of the button
      * @param className
      *   the class name to use in the button style
      * @return
      *   the html button
      */
    def createButton(title: String, className: String): Button =
      val button = dom.document.createElement("button").asInstanceOf[Button]
      button.textContent = title
      button.className = className
      button
    /** Create an html input
      * @param inputType
      *   the type of the input
      * @param length
      *   the length of the input
      * @return
      *   the html input
      */
    def createInput(inputType: String, length: Int): Input =
      val input = dom.document.createElement("input").asInstanceOf[Input]
      input.`type` = inputType
      input.size = length
      input

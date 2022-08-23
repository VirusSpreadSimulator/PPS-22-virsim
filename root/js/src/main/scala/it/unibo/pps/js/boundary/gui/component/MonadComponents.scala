package it.unibo.pps.js.boundary.gui.component

import it.unibo.pps.boundary.component.EventSource
import it.unibo.pps.boundary.component.Events.Event
import it.unibo.pps.js.boundary.gui.Values.BootstrapClasses
import monix.execution.Cancelable
import monix.reactive.{Observable, OverflowStrategy}
import org.scalajs.dom
import org.scalajs.dom.{HTMLDivElement, HTMLElement, HTMLOptionElement}
import org.scalajs.dom.html.{Button, Input, Select}

import java.awt.event.ActionEvent

object MonadComponents:
  import it.unibo.pps.js.boundary.gui.component.MonadComponents.Utils.{createButton, createInput}

  trait MonadButton extends EventSource:
    def button: Button
  object MonadButton:
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

  trait MonadSelect extends EventSource:
    def select: Select
  object MonadSelect:
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
        select.onchange = (e: dom.Event) => out.onNext(eventFactory(elems(select.selectedIndex)))
        Cancelable.empty
      }

  trait MonadConfigButton extends EventSource:
    def button: Button
    def input: Input
    def panel: HTMLElement
  object MonadConfigButton:
    def apply(title: String, configLenght: Int, eventFactory: String => Event): MonadConfigButton =
      val button = createButton(title, BootstrapClasses.BTN_SECONDARY)
      val input = createInput("text", configLenght)
      MonadConfigButtonImpl(button, input, eventFactory)

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
        button.addEventListener("click", (e: dom.MouseEvent) => out.onNext(eventFactory(input.value)))
        Cancelable.empty
      }

  private object Utils:
    def createButton(title: String, className: String): Button =
      val button = dom.document.createElement("button").asInstanceOf[Button]
      button.textContent = title
      button.className = className
      button
    def createInput(inputType: String, length: Int): Input =
      val input = dom.document.createElement("input").asInstanceOf[Input]
      input.`type` = inputType
      input.size = length
      input

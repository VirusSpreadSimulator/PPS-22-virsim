package it.unibo.pps.jvm.boundary.gui.component

import javax.swing.JFormattedTextField
import javax.swing.text.DefaultFormatterFactory

/** This module contains custom swing components */
object CustomSwingComponents:
  /** A JTextField that accept only integers between a [[min]] and a [[max]]
    * @param lenght
    *   the lenght of the field
    * @param min
    *   the minimum number accepted
    * @param max
    *   the maximum number accepted
    */
  class JNumericTextField(lenght: Int, min: Int, max: Int) extends JFormattedTextField:
    import javax.swing.text.NumberFormatter
    import java.text.NumberFormat

    private val integerFormat: NumberFormat = NumberFormat.getIntegerInstance
    private val numberFormatter: NumberFormatter = new NumberFormatter(integerFormat):
      override def stringToValue(text: String): AnyRef = if text.isEmpty then null else super.stringToValue(text)
    numberFormatter.setAllowsInvalid(false)
    numberFormatter.setMinimum(min)
    numberFormatter.setMaximum(max)
    setFormatterFactory(DefaultFormatterFactory(numberFormatter))
    setColumns(lenght)

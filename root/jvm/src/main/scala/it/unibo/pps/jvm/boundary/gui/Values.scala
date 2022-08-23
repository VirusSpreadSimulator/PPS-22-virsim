package it.unibo.pps.jvm.boundary.gui

import it.unibo.pps.jvm.boundary.gui.Utils

object Values:
  object Text:
    val SIMULATOR_NAME_SHORT: String = "Virsim"
    val SIMULATOR_NAME: String = "Virus Spread Simulator"
    val CHOOSE_FILE_BTN: String = "Choose"
    val START_BTN: String = "Start"
    val PAUSE_BTN: String = "Pause"
    val RESUME_BTN: String = "Resume"
    val STOP_BTN: String = "Stop"
    val COMMANDS_LABEL: String = "Commands"
    val DYNAMIC_CONFIG_LOG_LABEL: String = "Dynamic config values"
    val DYNAMIC_CONFIG_LABEL: String = "Dynamic config"
    val SWITCH_MASK_OBLIGATION: String = "Switch mask obligation"
    val VACCINE_ROUND: String = "Vaccine round"
    val SWITCH_STRUCTURE_OPEN: String = " Switch group "
    val STATS_LABEL: String = "Stats"
    val CONFIG_ERROR_TITLE: String = "Configuration error"
    val INVALID_FILE_LABEL: String = "Invalid file "
    val WRONG_PARAMETER_LABEL: String = "Wrong Parameter "
    val DAYS_LABEL_TITLE: String = "Days elapses: "
    val INFECTED_LABEL_TITLE: String = "Infected: "
    val SICK_LABEL_TITLE: String = "Sick: "
    val DEATHS_LABEL_TITLE: String = "Deaths: "
    val HOSPITAL_PRESSURE_LABEL_TITLE: String = "Hospital Pressure: "
    val ALIVE_LABEL_TITLE: String = "Alive: "
    val OPEN_STRUCTURE: String = "<i>open</i>"
    val CLOSED_STRUCTURE: String = "<i>close</i>"
    val MASK_STATUS_TITLE: String = "<b>Mask</b>"
    val STRUCTURES_GROUP_STATUS_TITLE = "<b>Group of structures</b>"
    val YES: String = "yes"
    val NO: String = "no"

  object Dimension:
    import Utils.given

    import java.awt.Dimension

    val INITGUI_WIDTH: Int = 500
    val INITGUI_HEIGHT: Int = 800
    val SIMULATIONGUI_WIDTH: Int = 920
    val SIMULATIONGUI_HEIGHT: Int = 850
    val SIMULATION_PANEL_MIN_DIMENSION: Dimension = (500, 500)
    val CHART_PANEL_MIN_DIMENSION: Dimension = (300, 500)
    val SIMULATION_GUI_TOP_DIMENSION: Dimension = (800, 500)
    val SIMULATION_GUI_BOTTOM_DIMENSION: Dimension = (800, 200)

  object Margin:
    import it.unibo.pps.jvm.boundary.gui.Utils.given

    import java.awt.Dimension

    val DEFAULT_HMARGIN: Dimension = (10, 0)
    val DEFAULT_VMARGIN: Dimension = (0, 5)

  object SimulationColor:
    import java.awt.Color
    val BACKGROUND_COLOR: Color = Color(56, 142, 60)
    val BACKGROUND_CHART_PANEL_COLOR: Color = Color(255, 204, 128)
    val HEALTHY_ENTITY_COLOR: Color = Color(118, 255, 3)
    val INFECTED_ENTITY_COLOR: Color = Color(221, 44, 0)
    val IMMUNITY_COLOR: Color = Color(13, 71, 161)
    val HOUSE_COLOR: Color = Color(144, 164, 174)
    val GENERIC_COLOR_OPEN: Color = Color(255, 145, 0)
    val GENERIC_COLOR_CLOSED: Color = Color(255, 183, 77)
    val HOSPITAL_COLOR: Color = Color(3, 169, 244)
    val VISIBILITY_RANGE_COLOR: Color = Color(212, 225, 87, 220)
    val STRUCTURE_CAPACITY_COLOR: Color = Color.RED

    def ageColor(color: Color, age: Int): Color =
      Color(
        Math.max(color.getRed - age / 3, 0),
        Math.max(color.getGreen - age / 3, 0),
        Math.max(color.getBlue - age / 3, 0)
      )

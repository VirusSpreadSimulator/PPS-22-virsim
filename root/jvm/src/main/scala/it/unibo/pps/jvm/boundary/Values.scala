package it.unibo.pps.jvm.boundary

object Values:
  object Text:
    val SIMULATOR_NAME_SHORT = "Virsim"
    val SIMULATOR_NAME = "Virus Spread Simulator"
    val CHOOSE_FILE_BTN = "Choose"
    val START_BTN = "Start"
    val PAUSE_BTN = "Pause"
    val STOP_BTN = "Stop"
    val COMMANDS_LABEL = "Commands"
    val DYNAMIC_CONFIG_LOG_LABEL = "Dynamic config values"
    val DYNAMIC_CONFIG_LABEL = "Dynamic config"
    val SWITCH_MASK_OBLIGATION = "Switch mask obligation"
    val VACCINE_ROUND = "Vaccine round"
    val SWITCH_STRUCTURE_OPEN = " Switch group "
    val STATS_LABEL = "Stats"
    val CONFIG_ERROR_TITLE = "Configuration error"
    val INVALID_FILE_LABEL = "Invalid file "
    val WRONG_PARAMETER_LABEL = "Wrong Parameter "

  object Dimension:
    val INITGUI_WIDTH = 500
    val INITGUI_HEIGHT = 800
    val SIMULATIONGUI_WIDTH = 920
    val SIMULATIONGUI_HEIGHT = 850
    val SIMULATION_PANEL_MIN_DIMENSION = (500, 500)
    val CHART_PANEL_MIN_DIMENSION = (300, 500)
    val SIMULATION_GUI_TOP_DIMENSION = (800, 500)
    val SIMULATION_GUI_BOTTOM_DIMENSION = (800, 200)

  object Margin:
    import it.unibo.pps.jvm.boundary.Utils.given
    import java.awt.Dimension

    val DEFAULT_HMARGIN: Dimension = (10, 0)
    val DEFAULT_VMARGIN: Dimension = (0, 5)

  object SimulationColor:
    import java.awt.Color
    val BACKGROUND_COLOR = Color(56, 142, 60)
    val BACKGROUND_CHART_PANEL_COLOR = Color(255, 204, 128)
    val HEALTHY_ENTITY_COLOR = Color(118, 255, 3)
    val INFECTED_ENTITY_COLOR = Color(221, 44, 0)
    val IMMUNITY_COLOR = Color(13, 71, 161)
    val HOUSE_COLOR = Color(144, 164, 174)
    val GENERIC_COLOR_OPEN = Color(255, 145, 0)
    val GENERIC_COLOR_CLOSED = Color(255, 183, 77)
    val HOSPITAL_COLOR = Color(3, 169, 244)
    val VISIBILITY_RANGE_COLOR = Color(212, 225, 87)

    def ageColor(color: Color, age: Int): Color =
      Color(
        Math.max(color.getRed - age / 3, 0),
        Math.max(color.getGreen - age / 3, 0),
        Math.max(color.getBlue - age / 3, 0)
      )

package it.unibo.pps.boundary.component

object Events:
  enum Event:
    case Hit(number: Int)
    case Pause
    case GuiClosed

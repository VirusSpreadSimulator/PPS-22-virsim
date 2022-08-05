package it.unibo.pps.jvm.boundary.gui

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.jvm.boundary.Utils
import it.unibo.pps.jvm.boundary.gui.InitGUI
import monix.eval.Task

import java.awt.{FlowLayout, Font, GridLayout}
import java.nio.file.Path
import javax.swing.*
import javax.swing.border.EmptyBorder

/** TEST FILE FOR INIT FRAME */
trait InitGUI:
  def init(): Task[Unit]
  def start(): Task[Unit]
  def config(): Task[Path]
  def error(): Task[Unit]

object InitGUI:
  def apply(width: Int = 500, height: Int = 800, title: String = "Virsim"): InitGUI = InitGUIImpl(width, height, title)
  private class InitGUIImpl(width: Int, height: Int, title: String) extends InitGUI:
    import Utils.given
    private lazy val fileSrcTextField: JTextField = JTextField(width / 25)

    private lazy val container: Task[JFrame] =
      for
        frame <- io(JFrame(title))
        _ <- io(frame.setMinimumSize((width, height)))
        _ <- io(frame.setSize(width, height))
        _ <- io(frame.setLocationRelativeTo(null))
      yield frame

    private lazy val titlePanel: Task[JPanel] =
      for
        panel <- io(JPanel())
        label <- io(JLabel())
        _ <- io(label.setText(Text.SIMULATORNAME))
        _ <- io(label.setFont(label.getFont.deriveFont(Font.BOLD, label.getFont.getSize2D * 3f)))
        _ <- io(panel.add(label))
      yield panel

    private lazy val filePanel: Task[JPanel] =
      for
        panel <- io(JPanel(FlowLayout(FlowLayout.CENTER)))
        chooseBtn <- io(JButton(Text.CHOOSEFILEBTN))
        _ <- io(panel.add(fileSrcTextField))
        _ <- io(panel.add(chooseBtn))
      yield panel

    private lazy val startPanel: Task[JPanel] =
      for
        panel <- io(JPanel())
        startBtn <- io(JButton(Text.STARTBTN))
        _ <- io(panel.add(startBtn))
      yield panel

    private lazy val mainPanel: Task[JPanel] =
      for
        mainPanel <- io(JPanel())
        mainLM <- io(GridLayout(3, 1))
        _ <- io(mainPanel.setLayout(mainLM))
        titleP <- titlePanel
        fileP <- filePanel
        startP <- startPanel
        _ <- io(mainPanel.add(titleP))
        _ <- io(mainPanel.add(fileP))
        _ <- io(mainPanel.add(startP))
        _ <- io(mainPanel.setBorder(EmptyBorder(height / 10, 0, height / 10, 0)))
      yield mainPanel

    override def init(): Task[Unit] =
      for
        frame <- container.asyncBoundary(Utils.swingScheduler)
        mainP <- mainPanel
        _ <- io(frame.setContentPane(mainP))
        _ <- io(frame.setVisible(true))
      yield ()

    override def start(): Task[Unit] = ???

    override def config(): Task[Path] = ???

    override def error(): Task[Unit] = ???

//  @main def main(): Unit =
//    import monix.execution.Scheduler
//    given Scheduler = monix.execution.Scheduler.global
//    InitGUI().init().runAsyncAndForget

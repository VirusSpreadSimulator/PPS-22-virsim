package it.unibo.pps.jvm.boundary.gui

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.jvm.boundary.Values.Text
import it.unibo.pps.jvm.boundary.Utils
import it.unibo.pps.jvm.boundary.gui.InitGUI
import monix.eval.Task

import java.awt.event.ActionEvent
import java.awt.{FlowLayout, Font, GridLayout}
import java.nio.file.Path
import javax.swing.*
import javax.swing.border.EmptyBorder
import scala.concurrent.Promise

/** TEST FILE FOR INIT FRAME */
trait InitGUI:
  def init(): Task[Unit]
  def config(): Task[Path]
  def error(err: ConfigurationError): Task[Unit]
  def start(simulation: SimulationGUI): Task[Unit]

object InitGUI:
  def apply(width: Int = 500, height: Int = 800, title: String = "Virsim"): InitGUI = InitGUIImpl(width, height, title)
  private class InitGUIImpl(width: Int, height: Int, title: String) extends InitGUI:
    import Utils.given
    private lazy val frame = JFrame(title)
    private lazy val startBtn = JButton(Text.STARTBTN)
    private lazy val fileChooser = JFileChooser()
    private lazy val fileSrcTextField: JTextField = JTextField(width / 25)
    private val filePromise = Promise[Path]()

    private lazy val container: Task[JFrame] =
      for
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
        _ <- io(chooseBtn.addActionListener((e: ActionEvent) => openFileDialog()))
        _ <- io(panel.add(fileSrcTextField))
        _ <- io(panel.add(chooseBtn))
      yield panel

    private lazy val startPanel: Task[JPanel] =
      for
        panel <- io(JPanel())
        _ <- io(startBtn.addActionListener((e: ActionEvent) => filePromise.success(Path.of(fileSrcTextField.getText))))
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

    override def config(): Task[Path] = Task.deferFuture(filePromise.future)

    override def error(err: ConfigurationError): Task[Unit] = Task.pure {}

    override def start(simulation: SimulationGUI): Task[Unit] =
      for
        _ <- io(frame.dispose())
        _ <- simulation.init()
      yield ()

    private def openFileDialog(): Unit = fileChooser.showOpenDialog(frame) match
      case JFileChooser.APPROVE_OPTION => fileSrcTextField.setText(fileChooser.getSelectedFile.getAbsolutePath)
      case _ =>

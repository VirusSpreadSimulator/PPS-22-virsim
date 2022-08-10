package it.unibo.pps.jvm.boundary.gui

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.jvm.boundary.Values.{Dimension, Text}
import it.unibo.pps.jvm.boundary.Utils
import it.unibo.pps.jvm.boundary.gui.InitGUI
import monix.eval.Task

import java.awt.event.ActionEvent
import java.awt.{FlowLayout, Font, GridLayout}
import java.nio.file.Path
import javax.swing.*
import javax.swing.border.EmptyBorder
import scala.concurrent.Promise

/** Interface that describe the init user interface for the simulation (JVM) */
trait InitGUI:
  /** Initialise the gui
    * @return
    *   the task
    */
  def init(): Task[Unit]
  /** This task allow the caller to obtain the path of the configuration file for the simulation. Useful for
    * [[it.unibo.pps.boundary.BoundaryModule.ConfigBoundary.config()]]
    * @return
    *   the task
    */
  def config(): Task[Path]
  /** This task allow the caller to show an error in the configuration. Useful for
    * [[it.unibo.pps.boundary.BoundaryModule.ConfigBoundary.error()]]
    * @param err
    *   the error in the configuration
    * @return
    *   the task
    */
  def error(err: ConfigurationError): Task[Unit]
  /** Handle the start of the simulation
    * @param simulation
    *   the simulation user interface to launch
    * @return
    *   the task
    */
  def start(simulation: SimulationGUI): Task[Unit]

object InitGUI:
  /** Factory to create an InitGUI
    * @param width
    *   the width of the window
    * @param height
    *   the height of the window
    * @param title
    *   the title of the window
    * @return
    *   the Init GUI.
    */
  def apply(
      width: Int = Dimension.INITGUI_WIDTH,
      height: Int = Dimension.INITGUI_HEIGHT,
      title: String = Text.SIMULATOR_NAME_SHORT
  ): InitGUI =
    InitGUIImpl(width, height, title)
  private class InitGUIImpl(width: Int, height: Int, title: String) extends InitGUI:
    import Utils.given
    private lazy val frame = JFrame(title)
    private lazy val fileChooser = JFileChooser()
    private lazy val fileSrcTextField: JTextField = JTextField(width / 25)
    private lazy val filePromise = Promise[Path]()

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
        _ <- io(label.setText(Text.SIMULATOR_NAME))
        _ <- io(label.setFont(label.getFont.deriveFont(Font.BOLD, label.getFont.getSize2D * 3f)))
        _ <- io(panel.add(label))
      yield panel

    private lazy val filePanel: Task[JPanel] =
      for
        panel <- io(JPanel(FlowLayout(FlowLayout.CENTER)))
        chooseBtn <- io(JButton(Text.CHOOSE_FILE_BTN))
        _ <- io(chooseBtn.addActionListener((e: ActionEvent) => openFileDialog()))
        _ <- io(panel.add(fileSrcTextField))
        _ <- io(panel.add(chooseBtn))
      yield panel

    private lazy val startPanel: Task[JPanel] =
      for
        panel <- io(JPanel())
        startBtn <- io(JButton(Text.START_BTN))
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
        _ <- io(frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))
        mainP <- mainPanel
        _ <- io(frame.setContentPane(mainP))
        _ <- io(frame.setVisible(true))
      yield ()

    override def config(): Task[Path] = Task.deferFuture(filePromise.future)

    override def error(err: ConfigurationError): Task[Unit] = for
      errorMessage <- io(err match
        case ConfigurationError.INVALID_FILE(message) => message
        case ConfigurationError.WRONG_PARAMETERS(message) => message
      )
      _ <- io(JOptionPane.showMessageDialog(frame, errorMessage, Text.CONFIG_ERROR_TITLE, JOptionPane.ERROR_MESSAGE))
    yield ()

    override def start(simulation: SimulationGUI): Task[Unit] =
      for
        _ <- io(frame.dispose())
        _ <- simulation.init()
      yield ()

    private def openFileDialog(): Unit = fileChooser.showOpenDialog(frame) match
      case JFileChooser.APPROVE_OPTION => fileSrcTextField.setText(fileChooser.getSelectedFile.getAbsolutePath)
      case _ =>

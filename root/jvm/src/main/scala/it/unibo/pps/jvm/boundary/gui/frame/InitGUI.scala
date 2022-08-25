package it.unibo.pps.jvm.boundary.gui.frame

import it.unibo.pps.boundary.ViewUtils.io
import it.unibo.pps.control.loader.configuration.ConfigurationComponent.ConfigurationError
import it.unibo.pps.control.parser.ReaderModule.{FilePath, StringFilePath}
import it.unibo.pps.jvm.boundary.gui.Values.{Dimension, Text}
import it.unibo.pps.jvm.boundary.gui.Utils
import monix.eval.Task
import monix.reactive.Consumer
import monix.reactive.subjects.PublishSubject
import java.awt.{FlowLayout, Font, GridLayout}
import javax.swing.*
import javax.swing.border.EmptyBorder

/** Interface that describes the init user interface for the simulation (JVM). */
trait InitGUI:
  /** Initialize the gui.
    * @return
    *   the task
    */
  def init(): Task[Unit]
  /** This task allows the caller to obtain the path of the configuration file for the simulation. Useful for
    * [[it.unibo.pps.boundary.BoundaryModule.ConfigBoundary.config]].
    * @return
    *   the task
    */
  def config(): Task[FilePath]
  /** This task allows the caller to show an error in the configuration. Useful for
    * [[it.unibo.pps.boundary.BoundaryModule.ConfigBoundary.error]].
    * @param errors
    *   the errors in the configuration
    * @return
    *   the task
    */
  def error(errors: Seq[ConfigurationError]): Task[Unit]
  /** Handle the start of the simulation.
    * @param simulation
    *   the simulation user interface to launch
    * @return
    *   the task
    */
  def start(simulation: SimulationGUI): Task[Unit]

object InitGUI:
  /** Factory to create an InitGUI.
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
    private lazy val fileSrcTextField: JTextField = JTextField(width / Dimension.FILE_SRC_TEXT_FIELD_WIDTH_REDUCER)
    private val fileChosen = PublishSubject[FilePath]() // Instead of a var with a Promise

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
        font <- io(label.getFont.deriveFont(Font.BOLD, label.getFont.getSize2D * Dimension.TITLE_FONT_MULTIPLIER))
        _ <- io(label.setFont(font))
        _ <- io(panel.add(label))
      yield panel

    private lazy val filePanel: Task[JPanel] =
      for
        panel <- io(JPanel(FlowLayout(FlowLayout.CENTER)))
        chooseBtn <- io(JButton(Text.CHOOSE_FILE_BTN))
        _ <- io(chooseBtn.addActionListener(_ => openFileDialog()))
        _ <- io(panel.add(fileSrcTextField))
        _ <- io(panel.add(chooseBtn))
      yield panel

    private lazy val startPanel: Task[JPanel] =
      for
        panel <- io(JPanel())
        startBtn <- io(JButton(Text.START_BTN))
        _ <- io(
          startBtn.addActionListener(_ =>
            val text = fileSrcTextField.getText
            if text.nonEmpty then fileChosen.onNext(StringFilePath(text))
          )
        )
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
        border <- io(
          EmptyBorder(height / Dimension.INITGUI_PADDING_FRACTION, 0, height / Dimension.INITGUI_PADDING_FRACTION, 0)
        )
        _ <- io(mainPanel.setBorder(border))
      yield mainPanel

    override def init(): Task[Unit] =
      for
        frame <- container.asyncBoundary(Utils.swingScheduler)
        _ <- io(frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))
        mainP <- mainPanel
        _ <- io(frame.setContentPane(mainP))
        _ <- io(frame.setVisible(true))
        _ <- Task.shift
      yield ()

    override def config(): Task[FilePath] = Task.defer(fileChosen.consumeWith(Consumer.head))

    override def error(errors: Seq[ConfigurationError]): Task[Unit] = for
      _ <- Task.pure {}.asyncBoundary(Utils.swingScheduler)
      errorMessage <- io(
        errors
          .map(err =>
            err match
              case ConfigurationError.INVALID_FILE(message) => Text.INVALID_FILE_LABEL + message
              case ConfigurationError.WRONG_PARAMETER(message) => Text.WRONG_PARAMETER_LABEL + message
          )
          .reduce(_ + "\n" + _)
      )
      _ <- io(JOptionPane.showMessageDialog(frame, errorMessage, Text.CONFIG_ERROR_TITLE, JOptionPane.ERROR_MESSAGE))
      _ <- Task.shift
    yield ()

    override def start(simulation: SimulationGUI): Task[Unit] =
      for
        _ <- io(frame.dispose())
        _ <- simulation.init()
      yield ()

    private def openFileDialog(): Unit = fileChooser.showOpenDialog(frame) match
      case JFileChooser.APPROVE_OPTION => fileSrcTextField.setText(fileChooser.getSelectedFile.getAbsolutePath)
      case _ =>

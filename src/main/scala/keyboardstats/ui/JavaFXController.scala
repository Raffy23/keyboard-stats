package keyboardstats.ui

import com.jfoenix.controls.{JFXButton, JFXDialog, JFXDialogLayout}
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import javafx.scene.text.{Font, Text}
import keyboardstats.ui.Defaults._
import keyboardstats.ui.Implicits._
import keyboardstats.util.i18n._
import scalafx.scene.control._
import scalafx.scene.layout.StackPane
import scalafx.scene.text.FontWeight
import scalafxml.core.macros.sfxml

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
@sfxml class JavaFXController(tabKeyboard: Tab, tabSettings: Tab, dialogStackPane: StackPane) {

  // Init Tabs
  initTab(tabKeyboard, MaterialIcon.KEYBOARD, "tooltip.keyboard".localize)
  initTab(tabSettings, MaterialIcon.SETTINGS, "tooltip.settings".localize)

  // BUG: These vals are not accessible after macro and if used as functions return value will
  //      be BoxedUnit:
  //          @new javafx.fxml.FXML() def keyboardController: scala.Unit = impl.keyboardController;
  private lazy val keyboardController: KeyboardController = tabKeyboard.content.value.getController
  private lazy val settingsController: SettingsController = tabSettings.content.value.getController

  // So we have to play proxy in this controller for all sub-controllers!
  def updateAfterLoaded(): Unit = keyboardController.updateAfterLoaded()
  def shutdownBackgroundTasks(): Unit = keyboardController.shutdownBackgroundTasks()



  private def initTab(tab: Tab, icon: MaterialIcon, tooltip: String): Unit = {
    tab.graphic = icon.toIcon(38.0)
    tab.tooltip = new Tooltip(tooltip).setDelay(TOOLTIP_DELAY)
  }

  def showDialog(heading: String, content: String, buttons: List[(String, () => Boolean)]): Unit = {
    import scala.collection.JavaConverters._

    val diagContent = new JFXDialogLayout()
    val dialog = new JFXDialog(dialogStackPane, diagContent, JFXDialog.DialogTransition.CENTER)
    val headingText = new Text(heading)
    headingText.setFont(Font.font(null, FontWeight.Bold, 14))

    diagContent.setHeading(new Text(heading))
    diagContent.setBody(new Text(content))
    diagContent.setActions(
      buttons.map { case (name, action) =>
        val btn = new JFXButton(name)
        btn.setButtonType(JFXButton.ButtonType.FLAT)
        btn.setStyle("-fx-background-color: #d5d5d5")
        btn.setOnAction((_) => if (action()) dialog.close())

        btn
      }.asJava
    )

    dialog.show()
  }

}
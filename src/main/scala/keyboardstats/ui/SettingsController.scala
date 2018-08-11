package keyboardstats.ui

import com.jfoenix.controls.{JFXButton, JFXComboBox, JFXTextField}
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import javafx.fxml.FXML
import javafx.scene.control.{ListCell => jfxListCell}
import keyboardstats.service.{InputGatherer, KeyboardLayoutService}
import keyboardstats.ui.Implicits._
import keyboardstats.util.UserConfig
import scalafx.beans.property.ReadOnlyBooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.control.{Label, ListCell, ListView, TextField}
import scalafx.scene.layout.{HBox, Pane, Priority}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._
import scala.language.implicitConversions

/**
  * Created by: 
  *
  * @author Raphael
  * @version 07.08.2018
  */
@sfxml class SettingsController(lvIgnoreApps: ListView[String], textIgnoreApp: TextField, @FXML btnIgnoreApp: JFXButton,
                                @FXML cbKeyboardLayout: JFXComboBox[String], @FXML btnSubmitChanges: JFXButton,
                                @FXML textExportPath: JFXTextField, @FXML textDefaultPath: JFXTextField) {

  lvIgnoreApps.items.get().addAll(InputGatherer.excludedApps)
  lvIgnoreApps.cellFactory = (param) => new ApplicationListCell(param.focused)

  textDefaultPath.textProperty().bindBidirectional(Defaults.DEFAULT_STATISTICS_PATH)
  textExportPath.textProperty().bindBidirectional(Defaults.DEFAULT_EXPORT_PATH)

  cbKeyboardLayout.getItems.addAll(KeyboardLayoutService.layouts.keys.asJavaCollection)
  cbKeyboardLayout.getSelectionModel.select(Defaults.DEFAULT_KEYBOARD_LAYOUT.value)
  cbKeyboardLayout.getSelectionModel.selectedItemProperty().addListener((_,_,newValue) => Defaults.DEFAULT_KEYBOARD_LAYOUT.value = newValue)

  btnIgnoreApp.onAction = (_) => {
    if (textIgnoreApp.text.value.nonEmpty) {
      InputGatherer.excludedApps.add(textIgnoreApp.text.value)
      lvIgnoreApps.items.get().add(textIgnoreApp.text.value)
    }

    textIgnoreApp.text.setValue("")
  }

  btnSubmitChanges.onAction = (_) => {
    UserConfig.save()
    UserConfig.saveExcludedApps()
  }



  private implicit def convertJFXListCellToSFXListCell[T](jfx: jfxListCell[T]): ListCell[T] = new ListCell[T](jfx)

  private class ApplicationListCell(focused: ReadOnlyBooleanProperty) extends jfxListCell[String] {
    private val hbox      = new HBox()
    private val textLabel = new Label("")
    private val icon      = MaterialIcon.DELETE.toIcon(16)
    private val button    = new JFXButton("", icon)
    private val filler    = new Pane()

    hbox.children.addAll(textLabel, filler, button)
    hbox.alignment = Pos.CenterLeft
    textLabel.vgrow = Priority.Always
    textLabel.alignmentInParent = Pos.CenterLeft
    HBox.setHgrow(filler, Priority.Always)

    button.setOnAction((_) => {
      InputGatherer.excludedApps.remove(textLabel.text.value)
      lvIgnoreApps.getItems.remove(this.getIndex)
    })

    focused.onChange((_,_,focused) => {
      if (!focused) icon.style = ""
      else if (focused && isSelected) icon.style = "-fx-fill: white"
    })

    override def updateItem(item: String, empty: Boolean): Unit = {
      super.updateItem(item, empty)
      this.setText(null)

      if (empty) setGraphic(null)
      else {
        textLabel.text = item
        setGraphic(hbox)
      }
    }

    override def updateSelected(selected: Boolean): Unit = {
      super.updateSelected(selected)

      if (selected) icon.style = "-fx-fill: white"
      else          icon.style = ""
    }

  }

}
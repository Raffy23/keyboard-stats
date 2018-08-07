package keyboardstats.ui

import com.jfoenix.controls.{JFXButton, JFXComboBox, JFXTextField}
import javafx.fxml.FXML
import keyboardstats.service.{InputGatherer, KeyboardLayoutService}
import scalafx.scene.control.{ListView, TextField}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._
import Implicits._
import keyboardstats.util.UserConfig

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

  btnSubmitChanges.onAction = (_) => UserConfig.save()

}

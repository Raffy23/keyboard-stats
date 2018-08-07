package keyboardstats.ui

import keyboardstats.service.InputGatherer
import scalafx.scene.control.{Button, ListView, TextField}
import scalafxml.core.macros.sfxml

/**
  * Created by: 
  *
  * @author Raphael
  * @version 07.08.2018
  */
@sfxml class SettingsController(lvIgnoreApps: ListView[String], textIgnoreApp: TextField, btnIgnoreApp: Button) {

  lvIgnoreApps.items.get().addAll(InputGatherer.excludedApps)

  btnIgnoreApp.onAction = (_) => {
    if (textIgnoreApp.text.value.nonEmpty) {
      InputGatherer.excludedApps.add(textIgnoreApp.text.value)
      lvIgnoreApps.items.get().add(textIgnoreApp.text.value)
    }

    textIgnoreApp.text.setValue("")
  }

}

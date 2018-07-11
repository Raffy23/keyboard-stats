package kbs.util

import javafx.scene.input.KeyCode
import kbs.util.KeyEventListener.KeyEventType


/**
  * Created by: 
  *
  * @author Raphael
  * @version 11.07.2018
  */
abstract class KeyEventListener {
  def eventOccurred(event: KeyEventType, keyCode: KeyCode): Unit
}

object KeyEventListener {
  sealed trait KeyEventType
  object KEY_PRESSED extends KeyEventType
  object KEY_RELEASED extends KeyEventType
}

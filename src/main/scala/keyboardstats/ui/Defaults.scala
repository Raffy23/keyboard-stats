package keyboardstats.ui

import keyboardstats.util.AppConfig

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
object Defaults {
  val TOOLTIP_DELAY = 250
  lazy val DEFAULT_KEYBOARD_LAYOUT: String = AppConfig.conf.keyboardLayout
  val DEFAULT_SAVE_PATH = "./output"
}

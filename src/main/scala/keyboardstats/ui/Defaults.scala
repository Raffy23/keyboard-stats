package keyboardstats.ui

import keyboardstats.util.AppConfig
import scalafx.beans.property.StringProperty

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
object Defaults {
  val TOOLTIP_DELAY = 250

  val DEFAULT_KEYBOARD_LAYOUT: StringProperty = StringProperty(AppConfig.conf.keyboardLayout)
  val DEFAULT_EXPORT_PATH: StringProperty = StringProperty("./output")
  val DEFAULT_STATISTICS_PATH: StringProperty = StringProperty("./statistics")

  lazy val EXCLUDED_APPS_FILE: String = AppConfig.conf.configDirectory + "excluded_apps.json"
  lazy val USER_CONFIG_FILE: String = AppConfig.conf.configDirectory + "user_config.json"
}

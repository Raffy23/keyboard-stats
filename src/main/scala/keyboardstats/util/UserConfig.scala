package keyboardstats.util

import java.io.{File, PrintWriter}

import keyboardstats.ui.Defaults

import scala.io.Source
import Defaults._
import keyboardstats.service.InputGatherer

/**
  * Created by: 
  *
  * @author Raphael
  * @version 07.08.2018
  */
object UserConfig {

  import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  private case class UserConfigData(statisticsPath: String, exportPath: String, keyboardLayout: String)

  private def usrConfig = new File(Defaults.USER_CONFIG_FILE)

  def load(): Unit = {
    decode[UserConfigData](Source.fromFile(usrConfig).getLines().mkString).foreach(config => {
      DEFAULT_STATISTICS_PATH.value = config.statisticsPath
      DEFAULT_KEYBOARD_LAYOUT.value = config.keyboardLayout
      DEFAULT_EXPORT_PATH.value = config.exportPath
    })
  }

  def save(): Unit = {
    val writer = new PrintWriter(usrConfig)
    writer.print(
      UserConfigData(
        DEFAULT_STATISTICS_PATH.value,
        DEFAULT_EXPORT_PATH.value,
        DEFAULT_KEYBOARD_LAYOUT.value
      ).asJson
       .noSpaces
    )
    writer.flush()
    writer.close()
  }

  def saveExcludedApps(): Unit = {
    import io.circe.syntax._
    import scala.collection.JavaConverters._

    val writer = new PrintWriter(new File(EXCLUDED_APPS_FILE))
    writer.print(InputGatherer.excludedApps.asScala.asJson.noSpaces)
    writer.flush()
    writer.close()
  }

}

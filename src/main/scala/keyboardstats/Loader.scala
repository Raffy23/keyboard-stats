package keyboardstats

import java.awt.{MenuItem, PopupMenu, SystemTray, TrayIcon}
import java.io.{File, PrintWriter}
import java.time.LocalDate
import java.util.Locale

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import keyboardstats.service.{InputGatherer, SingleInstanceService, Statistics}
import keyboardstats.util.{AppConfig, KeyEventListener}
import keyboardstats.util.i18n.LanguageManager
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform
import scalafx.scene.control.Alert

import scala.io.Source
import util.i18n._
import ui.Implicits._
import scalafx.embed.swing.SwingFXUtils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import keyboardstats.ui.Defaults._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.07.2018
  */
object Loader extends App {

  // Load language settings
  AppConfig.conf.language.foreach(lang => Locale.setDefault(Locale.forLanguageTag(lang)))
  if (!LanguageManager.isCurrentLangSupported) {
    System.err.println(s"ERROR: Locale '${Locale.getDefault}' is not supported, falling back to ${Locale.ENGLISH}!")
    Locale.setDefault(Locale.ENGLISH)
  }

  private var uiPresent = false
  private val instanceMutex = new SingleInstanceService("show", {
    case "show" => showUI()
  })

  if (!instanceMutex.isFirstInstance) {
    instanceMutex.destroy()
    println("Calling show of started instance ...")

    System.exit(0)
  }

  Platform.implicitExit = false
  mkdirIfAbsent(AppConfig.conf.statisticsDirectory)
  mkdirIfAbsent(AppConfig.conf.configDirectory)

  loadExcludedApps()
  loadUserConfig()

  InputGatherer.globalKeyListener.start()
  InputGatherer.listeners.add((_: KeyEventListener.KeyEventType, keyCode: Int, app: String) =>
    Statistics.logKeyPress(LocalDate.now(), app, keyCode)
  )

  if (!SystemTray.isSupported) {
    println("alert.systray_not_supported".localize)
    Platform.runLater(() => new Alert(Alert.AlertType.Error, "alert.systray_not_supported".localize).show())
  } else {
    new JFXPanel() // boot JavaFX Toolkit
    Platform.runLater(() => {

      val dim = SystemTray.getSystemTray.getTrayIconSize.height

      val popup = new PopupMenu()
      val trayIcon = new TrayIcon(SwingFXUtils.fromFXImage(MaterialDesignIcon.KEYBOARD.toImage(dim), null))
      val exitItem = new MenuItem("tray.exit".localize)
      exitItem.addActionListener((_) => {
        if (uiPresent)
          Platform.runLater(() => MainJFXWindow.stage.close())

        destroy()
      })

      popup.add(exitItem)
      trayIcon.setPopupMenu(popup)
      trayIcon.addActionListener((_) => showUI())

      SystemTray.getSystemTray.add(trayIcon)
    })
  }

  if (args.contains("--start-background-service")) {
    println("Started minimized in systemtry!")
  } else {
    showUI()
  }

  def destroy(): Unit = {
    instanceMutex.destroy()
    InputGatherer.globalKeyListener.destroy()

    Statistics.syncToDisk()()

    import io.circe.syntax._
    import scala.collection.JavaConverters._

    val writer = new PrintWriter(new File(EXCLUDED_APPS_FILE))
    writer.print(InputGatherer.excludedApps.asScala.asJson.noSpaces)
    writer.flush()
    writer.close()

    Platform.exit()
    System.exit(0) // AWT Event Queue still running, how kill?
  }

  private def showUI(): Unit =
    if (uiPresent) {
      Platform.runLater(() => MainJFXWindow.stage.show())
    } else {
      uiPresent = true
      Future { MainJFXWindow.main(args) }
    }



  private def mkdirIfAbsent(path: String): Unit = {
    val folder = new File(path)
    if (!folder.exists())
      folder.mkdirs()
  }

  private def loadExcludedApps(): Unit = {
    if (new File(EXCLUDED_APPS_FILE).exists()) {
      import io.circe.parser._

      import scala.collection.JavaConverters._
      InputGatherer.excludedApps.addAll(
        decode[List[String]](Source.fromFile(EXCLUDED_APPS_FILE).getLines().mkString).fold(
          (_) => List.empty[String],
          (v) => v
        ).asJavaCollection
      )
    }
  }

  private def loadUserConfig(): Unit = {
    if (new File(USER_CONFIG_FILE).exists()) {
      //TODO:
      println("Read USER_CONFIG_FILE not implemented")
    }
  }

}

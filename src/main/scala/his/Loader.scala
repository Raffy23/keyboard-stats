package his

import java.awt.{MenuItem, PopupMenu, SystemTray, TrayIcon}
import java.io.{File, PrintWriter}
import java.time.LocalDate
import java.util.Locale

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import his.service.{InputGatherer, SingleInstanceService, Statistics}
import his.util.{AppConfig, KeyEventListener}
import his.util.i18n.LanguageManager
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform
import scalafx.scene.control.Alert

import scala.io.Source
import util.i18n._
import ui.Implicits._
import scalafx.embed.swing.SwingFXUtils

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
    Locale.setDefault(Locale.ENGLISH)
    println("Error Locale is not supported, falling back to english!")
  }

  private var uiPresent = false
  private val instanceMutex = new SingleInstanceService("show", {
    case "show" if uiPresent  => Platform.runLater(() => MainJFXWindow.stage.show())
    case "show" if !uiPresent => MainJFXWindow.main(args); uiPresent = true
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

  InputGatherer.globalKeyListener.start()
  InputGatherer.listeners.add((_: KeyEventListener.KeyEventType, keyCode: Int, app: String) =>
    Statistics.logKeyPress(LocalDate.now(), app, keyCode)
  )

  if (!SystemTray.isSupported) {
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
      trayIcon.addActionListener((_) => Platform.runLater(() => MainJFXWindow.stage.show()))

      SystemTray.getSystemTray.add(trayIcon)
    })
  }

  if (args.contains("--start-background-service")) {
    println("Started minimized in systemtry!")
  } else {
    uiPresent = true
    MainJFXWindow.main(args)
  }

  def destroy(): Unit = {
    instanceMutex.destroy()
    InputGatherer.globalKeyListener.destroy()

    Statistics.syncToDisk()()

    import io.circe.syntax._
    import scala.collection.JavaConverters._

    val writer = new PrintWriter(new File(AppConfig.conf.configDirectory + "excluded_apps.json"))
    writer.print(InputGatherer.excludedApps.asScala.asJson.noSpaces)
    writer.flush()
    writer.close()

    Platform.exit()
    System.exit(0) // AWT Event Queue still running, how kill?
  }


  private def mkdirIfAbsent(path: String): Unit = {
    val folder = new File(path)
    if (!folder.exists())
      folder.mkdirs()
  }

  private def loadExcludedApps(): Unit = {
    if (new File(AppConfig.conf.configDirectory + "excluded_apps.json").exists()) {
      import io.circe.parser._

      import scala.collection.JavaConverters._
      InputGatherer.excludedApps.addAll(
        decode[List[String]](Source.fromFile(AppConfig.conf.configDirectory + "excluded_apps.json").getLines().mkString).fold(
          (_) => List.empty[String],
          (v) => v
        ).asJavaCollection
      )
    }
  }

}

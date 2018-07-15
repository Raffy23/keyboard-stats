package his

import java.awt.event.KeyEvent
import java.awt.{AWTEvent, Toolkit}
import java.io.{File, PrintWriter}
import java.time.LocalDate
import java.util.Locale

import his.service.{InputGatherer, Statistics}
import his.ui.{CustomFXMLLoader, JavaFXController}
import his.util.KeyEventListener
import his.util.i18n.LanguageManager
import javafx.{scene => jfxs}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.NoDependencyResolver

import scala.io.Source

/**
  * Created by: 
  *
  * @author Raphael
  * @version 10.07.2018
  */
object Main extends JFXApp {

  // Check language, otherwise might crash
  if (!LanguageManager.isCurrentLangSupported) {
    Locale.setDefault(Locale.ENGLISH)
    println("Error Locale is not supported, falling back to english!")
  }

  // Check directories
  mkdirIfAbsent("./statistics/")
  mkdirIfAbsent("./config/")
  if (new File("./config/excluded_apps.json").exists()) {
    import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
    import scala.collection.JavaConverters._
    InputGatherer.badApps.addAll(
      decode[List[String]](Source.fromFile("./config/excluded_apps.json").getLines().mkString).fold(
        (_) => List.empty[String],
        (v) => v
      ).asJavaCollection
    )
  }

  InputGatherer.globalKeyListener.start()
  InputGatherer.listeners.add((_: KeyEventListener.KeyEventType, keyCode: Int, app: String) =>
    Statistics.logKeyPress(LocalDate.now(), app, keyCode)
  )

  private val loader = new CustomFXMLLoader(
    getClass.getResource("/javafx/main_layout.fxml"),
    NoDependencyResolver
  )

  loader.load()
  private val controller: JavaFXController = loader.getController[JavaFXController]

  private val WINDOW_WIDTH   = 893
  private val WINDOW_HEIGHT = 528


  stage = new PrimaryStage {
    title = "hInput - Heatmap"
    width = WINDOW_WIDTH
    height = WINDOW_HEIGHT

    minWidth = WINDOW_WIDTH
    minHeight = WINDOW_HEIGHT

    scene = new Scene(loader.getScalaFXRoot[jfxs.layout.AnchorPane])
  }

  stage.onCloseRequest = (_) => {
    controller.shutdownBackgroundTasks()
    InputGatherer.globalKeyListener.destroy()

    Statistics.syncToDisk()()

    import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
    import scala.collection.JavaConverters._

    val writer = new PrintWriter(new File("./config/excluded_apps.json"))
    writer.print(InputGatherer.badApps.asScala.asJson.noSpaces)
    writer.flush()
    writer.close()
  }


  def mkdirIfAbsent(path: String): Unit = {
    val folder = new File(path)
    if (!folder.exists())
      folder.mkdirs()
  }

}
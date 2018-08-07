package keyboardstats

import javafx.{scene => jfxs}
import keyboardstats.service.Statistics
import keyboardstats.ui.{CustomFXMLLoader, JavaFXController, KeyboardController}
import keyboardstats.util.i18n._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.NoDependencyResolver

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by: 
  *
  * @author Raphael
  * @version 10.07.2018
  */
object MainJFXWindow extends JFXApp {

  private val loader = new CustomFXMLLoader(getClass.getResource("/javafx/main_layout.fxml"), NoDependencyResolver)
  try { loader.load() } catch { case ex: Exception => ex.printStackTrace(); Loader.destroy() }

  private val controller: JavaFXController = loader.getController[JavaFXController]
  Statistics.syncFromDisk()().andThen { case (_) => controller.updateAfterLoaded() }

  private val WINDOW_WIDTH  = 893
  private val WINDOW_HEIGHT = 528

  stage = new PrimaryStage {
    title = "Keyboard Statistics"
    width = WINDOW_WIDTH
    height = WINDOW_HEIGHT

    minWidth = WINDOW_WIDTH
    minHeight = WINDOW_HEIGHT

    scene = new Scene(loader.getScalaFXRoot[jfxs.layout.AnchorPane])
  }

  stage.onCloseRequest = (event) => {
    controller.shutdownBackgroundTasks()
    event.consume()

    controller.showDialog(
      "alert.close_or_minimize.heading".localize,
      "alert.close_or_minimize.content".localize,
      List(
        ("minimize".localize, () => { stage.hide(); true }),
        ("close".localize, () => { stage.hide(); Loader.destroy(); true })
      )
    )
  }

}
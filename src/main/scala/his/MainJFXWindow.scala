package his

import his.service.Statistics
import his.ui.{CustomFXMLLoader, JavaFXController}
import javafx.{scene => jfxs}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, ButtonType}
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
  loader.load()

  private val controller: JavaFXController = loader.getController[JavaFXController]
  Statistics.syncFromDisk()().andThen { case (_) => controller.updateAfterLoaded() }

  private val WINDOW_WIDTH  = 893
  private val WINDOW_HEIGHT = 528

  stage = new PrimaryStage {
    title = "hInput - Heatmap"
    width = WINDOW_WIDTH
    height = WINDOW_HEIGHT

    minWidth = WINDOW_WIDTH
    minHeight = WINDOW_HEIGHT

    scene = new Scene(loader.getScalaFXRoot[jfxs.layout.AnchorPane])
  }

  stage.onCloseRequest = (event) => {
    controller.shutdownBackgroundTasks()
    event.consume()

    val alert = new Alert(Alert.AlertType.Confirmation, "alert.close_or_minimize", ButtonType.OK, ButtonType.Close).showAndWait()
    alert.foreach {
      case ButtonType.OK => stage.hide()
      case ButtonType.Close => Loader.destroy()
    }
  }

}
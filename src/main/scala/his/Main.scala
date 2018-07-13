package his

import java.util.Locale

import his.service.InputGatherer
import his.ui.{CustomFXMLLoader, JavaFXController}
import his.util.i18n.LanguageManager
import javafx.{scene => jfxs}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.NoDependencyResolver

/**
  * Created by: 
  *
  * @author Raphael
  * @version 10.07.2018
  */
object Main extends JFXApp {

  if (!LanguageManager.isCurrentLangSupported) {
    Locale.setDefault(Locale.ENGLISH)
    println("Error Locale is not supported, falling back to english!")
  }

  InputGatherer.globalKeyListener.start()

  private val loader = new CustomFXMLLoader(
    getClass.getResource("/javafx/main_layout.fxml"),
    NoDependencyResolver
  )

  loader.load()
  private val controller: JavaFXController = loader.getController[JavaFXController]

  private val WINDOW_WIDTH   = 865
  private val WINDOW_HEIGHT = 500


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
  }

}
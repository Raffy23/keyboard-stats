package his.ui

import java.net.URL

import his.util.i18n.LanguageManager
import javafx.{fxml => jfxf, scene => jfxs}
import scalafx.scene.Parent
import scalafxml.core.{ControllerDependencyResolver, FxmlProxyGenerator}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 27.02.2018
  */
class CustomFXMLLoader(fxml: URL, dependencies: ControllerDependencyResolver)
  extends jfxf.FXMLLoader(
    fxml,
    LanguageManager.bundle,
    new jfxf.JavaFXBuilderFactory(),
    (cls: Class[_]) => FxmlProxyGenerator(cls, dependencies)
  ) {

  //override def getController[T](): T = super.getController[ControllerAccessor].as[T]

  def getScalaFXRoot[T <: jfxs.Parent]: Parent = new ParentWrapper[T](getRoot[T])

  private class ParentWrapper[T <: jfxs.Parent](d: T) extends Parent(d)
}

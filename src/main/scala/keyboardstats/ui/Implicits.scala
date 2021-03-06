package keyboardstats.ui

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import com.jfoenix.controls.{JFXTreeTableView, _}
import de.jensd.fx.glyphs.{GlyphIcon, GlyphIcons}
import de.jensd.fx.glyphs.fontawesome.{FontAwesomeIcon, FontAwesomeIconView}
import de.jensd.fx.glyphs.materialdesignicons.{MaterialDesignIcon, MaterialDesignIconView}
import de.jensd.fx.glyphs.materialicons.{MaterialIcon, MaterialIconView}
import javafx.animation.{KeyFrame, Timeline}
import javafx.scene.text.Font
import scalafx.scene.{Node, SnapshotParameters}
import scalafx.scene.control._
import javafx.scene.image.{Image => jfxImage}
import javafx.scene.{Scene => jfxScene}
import scalafx.beans.property.StringProperty
import scalafx.event.ActionEvent
import scalafx.scene.image.Image
import scalafx.scene.paint.Color
import scalafx.stage.Window
import scalafx.util.Duration

import scala.language.implicitConversions

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
object Implicits {

  implicit class sfxJFXTabPaneClass(jFXTabPane: JFXTabPane) extends Node(jFXTabPane) {}
  implicit class sfxJFXListViewClass[T](jFXListView: JFXListView[T]) extends ListView[T](jFXListView) {}
  implicit class sfxJFXNodesListClass(jFXNodesList: JFXNodesList) extends Node(jFXNodesList) {}
  implicit class sfxJFXButtonClass(jFXButton: JFXButton) extends Button(jFXButton) {}
  implicit class sfxJFXTreeTableColumnClass[S,T](jFXTreeTableColumn: JFXTreeTableColumn[S,T]) extends TreeTableColumn[S,T] {}
  implicit class sfxJFXTreeTableView[T <: RecursiveTreeObject[T]](jFXTreeTableView: JFXTreeTableView[T]) extends TreeTableView[T] {}
  implicit class sfxJFXComboBox[T](jFXComboBox: JFXComboBox[T]) extends ComboBox[T] {}
  implicit class sfxJFXTextField(jFXTextField: JFXTextField) extends TextField {}

  // Doesn't work as indented
  implicit class sfxJFXDatePicker(jfxDatePicker: JFXDatePicker) extends Node(jfxDatePicker) {}

  implicit class sfxFontAwesomeIconView(fontAwesomeIcon: FontAwesomeIconView) extends Node(fontAwesomeIcon) {}
  implicit class sfxMaterialIconView(materialIconView: MaterialIconView) extends Node(materialIconView) {}

  implicit class FontAwesomeIconConverter(icon: FontAwesomeIcon) {
    def toIcon(size: Double = GlyphIcon.DEFAULT_ICON_SIZE): FontAwesomeIconView = {
      val result = new FontAwesomeIconView(icon)
      result.setGlyphSize(size)

      result
    }
  }

  implicit class MaterialDesignIconsConverter(icon: MaterialDesignIcon) {
    def toIcon(size: Double = GlyphIcon.DEFAULT_ICON_SIZE): MaterialDesignIconView = {
      val result = new MaterialDesignIconView(icon)
      result.setGlyphSize(size)

      result
    }

    def toImage(size: Double = GlyphIcon.DEFAULT_ICON_SIZE): Image = new Image(toIcon(size).toImage)

  }

  implicit class MaterialIconConverter(icon: MaterialIcon) {
    def toIcon(size: Double = GlyphIcon.DEFAULT_ICON_SIZE): MaterialIconView = {
      val result = new MaterialIconView(icon)
      // BUG: in MaterialIconView, font is not set correctly!
      result.setFont(new Font("Material Icons", size))
      result.setGlyphSize(size)

      result
    }
  }


  implicit class HakyTooltipClass(tooltip: Tooltip) {
    def setDelay(duration: Duration): Tooltip = {
      try {
        val fieldBehavior = tooltip.delegate.getClass.getDeclaredField("BEHAVIOR")
        fieldBehavior.setAccessible(true)

        val objBehavior = fieldBehavior.get(tooltip.delegate)
        val fieldTimer = objBehavior.getClass.getDeclaredField("activationTimer")
        fieldTimer.setAccessible(true)

        val objTimer = fieldTimer.get(objBehavior).asInstanceOf[Timeline]

        objTimer.getKeyFrames.clear()
        objTimer.getKeyFrames.add(new KeyFrame(duration))
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }

      tooltip
    }
  }

  implicit def integerToDuration(int: Int): Duration = new Duration(int)

  implicit class SnapshotableGlyphIcon[T <: Enum[T] with GlyphIcons](node: GlyphIcon[T]) {
    def toImage: jfxImage = {
      val label = new Label()
      label.setGraphic(node)

      val scene = new jfxScene(label)
      scene.setFill(Color.Transparent)

      val params = new SnapshotParameters()
      params.setFill(Color.Transparent)

      node.snapshot(params, null)
    }
  }

  implicit def stylesheet(name: String): String = getClass.getResource("/javafx/style/"+name+".css").toExternalForm

  implicit class RichJavaControllerNode(node: javafx.scene.Node) {
    def getController[T]: T = node.getProperties.get("controller").asInstanceOf[T]
  }
  implicit class RichScalaFXControllerNode(node: Node) {
    def getController[T]: T = node.getProperties.get("controller").asInstanceOf[T]
  }

  implicit class RichActionEvent(event: ActionEvent) {
    import javafx.stage.{Window => jfxWindow}
    import javafx.scene.{Node => jfxNode}

    private class WindowInstance(jfxWindow: jfxWindow) extends Window(jfxWindow) { }
    private implicit def convertJfxWindowToSfxWindow(window: jfxWindow): Window = new WindowInstance(window)

    def getStage: Window = event.source.asInstanceOf[jfxNode].getScene.getWindow
  }

  implicit def stringPropertyToString(property: StringProperty): String = property.value

  implicit def jfxEventConverter(event: javafx.event.ActionEvent): ActionEvent = new ActionEvent(event)

}

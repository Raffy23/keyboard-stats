package keyboardstats

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import keyboardstats.ui.Defaults.TOOLTIP_DELAY
import keyboardstats.ui.Implicits._
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.Tooltip

import scala.collection.concurrent.TrieMap
import scala.util.Try

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.07.2018
  */
package object ui {

  class KeyDataProperty(keyCodeValue: Int, backend: TrieMap[Int, Long]) extends RecursiveTreeObject[KeyDataProperty] {
    val keyCode = ObjectProperty(keyCodeValue)
    val count = ObjectProperty(backend.getOrElseUpdate(keyCodeValue, 0))

    // backend can not be reactive anymore if multiple records are selected form statistics ...
    def refresh(): Unit = count.value += 1
  }

  def FAB(icon: MaterialIcon, tooltip: Tooltip, iconSize: Int = 38): JFXButton = {
    val button = new JFXButton()
    val graphic = icon.toIcon(iconSize)
    graphic.setStyle("-fx-fill: #F0F0F0;")

    button.setGraphic(graphic)
    button.setTooltip(new Tooltip(tooltip).setDelay(TOOLTIP_DELAY))
    button.setButtonType(JFXButton.ButtonType.RAISED)
    button.getStyleClass.addAll("fab")

    button
  }

  def SmallFAB(icon: MaterialIcon, tooltip: String, action: () => Unit = () => {}, iconSize: Int = 21): JFXButton = {
    val button = new JFXButton()
    val graphic = icon.toIcon(iconSize)
    graphic.setStyle("-fx-fill: #F0F0F0;")

    button.setGraphic(graphic)
    button.setTooltip(new Tooltip(tooltip).setDelay(TOOLTIP_DELAY))
    button.setButtonType(JFXButton.ButtonType.RAISED)
    button.getStyleClass.addAll("fab-small")
    button.onAction = (_) => action()

    button
  }

  def jfxButton(text: String, tooltip: String, style: String = "fab", action: () => Unit): JFXButton = {
    val button = new JFXButton(text)
    button.setTooltip(new Tooltip(tooltip).setDelay(TOOLTIP_DELAY))
    button.setButtonType(JFXButton.ButtonType.RAISED)
    button.getStyleClass.add(style)
    button.onAction = (_) => action()

    button
  }



}

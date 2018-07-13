package his.ui

import com.jfoenix.controls.JFXTabPane
import de.jensd.fx.glyphs.GlyphIcon
import de.jensd.fx.glyphs.fontawesome.{FontAwesomeIcon, FontAwesomeIconView}
import javafx.animation.{KeyFrame, Timeline}
import scalafx.scene.Node
import scalafx.scene.control.Tooltip
import scalafx.util.Duration

import scala.language.implicitConversions

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
object Implicits {

  implicit class sfxJFXTabPane(jFXTabPane: JFXTabPane) extends Node(jFXTabPane) { }
  implicit class sfxFontAwesomeIconView(fontAwesomeIcon: FontAwesomeIconView) extends Node(fontAwesomeIcon) { }

  implicit class FontAwesomeIconConverter(icon: FontAwesomeIcon) {
    def toIcon(size: Double = GlyphIcon.DEFAULT_ICON_SIZE): Node = {
      val result = new FontAwesomeIconView(icon)
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


}

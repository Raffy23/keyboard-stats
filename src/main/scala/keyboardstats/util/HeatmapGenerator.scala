package keyboardstats.util

import scalafx.application.Platform
import scalafx.scene.paint.Color
import scalafx.scene.web.WebEngine

import scala.collection.concurrent.TrieMap
import scala.language.implicitConversions
import scala.util.Try
import scala.xml._
import scala.xml.transform.{RewriteRule, RuleTransformer}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 12.07.2018
  */
class HeatmapGenerator(keymap: Seq[Node], keys: TrieMap[Int, Long], startColor: Color = Color.White, endColor: Color = Color.Red) {

  private val RAMP_VALUE_1 = 0.20
  private val RAMP_VALUE_2 = 0.08

  private var deadKeys = Set.empty[Int]

  implicit class DoubleColorValue(d: Double) {
    def toIntColor: Int = (d * 255).toInt
    def toColorString: String = d.toIntColor.toHexString.toUpperCase
  }

  implicit class HexColor(color: Color) {
    def toHexString: String = s"#${color.red.toColorString}${color.green.toColorString}${color.blue.toColorString}FF"
    def toRGB: String = s"rgb(${color.red.toIntColor},${color.green.toIntColor},${color.blue.toIntColor})"
  }

  def update(key: Int): Unit = keys.put(key, keys.getOrElse(key, 0L) + 1L)

  def transform(): Seq[Node] = new RuleTransformer(xmlColoringRule(max)).transform(keymap)

  def transform(engine: WebEngine): Unit = {
    if (engine.document == null) {
      System.err.println("Error: document is null, push event again into event queue!")
      Platform.runLater(() => transform(engine))

      return
    }

    keys.foreach { case (keyCode, count) =>
      val elem = engine.document.getElementById(s"0x${keyCode.toHexString.toUpperCase}")

      if (elem == null) {
        if (!deadKeys.contains(keyCode)) {
          System.err.println(s"Unable to refresh key 0x${keyCode.toHexString.toUpperCase}, not found in keymap!")
          deadKeys += keyCode
        }
      } else {
        elem.setAttribute("style", s"fill:${color(keyCode, count).toRGB};fill-opacity:1;stroke:#202326;stroke-width:0")
      }
    }
  }

  private def color(implicit keyCode: Int, count: Long): Color = {
    var p = count.asInstanceOf[Double] / max
    var s = startColor
    var c = endColor

    if (p <= RAMP_VALUE_1 && p >= RAMP_VALUE_2) {
      c = Color.Orange
      p /= RAMP_VALUE_1
      s = Color.Green
    } else if (p <= RAMP_VALUE_2) {
      c = Color.Green
      p /= RAMP_VALUE_2
    } else {
      s = Color.Orange
    }

    s.interpolate(c, p)
  }

  private def max: Long = if(keys.nonEmpty) keys.maxBy(_._2)._2 else 1L

  private def xmlColoringRule(max: Double) = new RewriteRule {

    implicit def nodeToString(n: NodeSeq): String = n.text

    implicit class AttributeCopyTranform(attr: Attribute) {
      def copyData(key: String = attr.key, value: Any = attr.value): Attribute =
        Attribute(attr.pre, key, Text(value.toString), attr.next)
    }

    implicit def iterableToMetaData(items: Iterable[MetaData]): MetaData = {
      items match {
        case Nil => Null
        case head :: tail => head.copy(next=iterableToMetaData(tail))
      }
    }

    override def transform(n: Node): Seq[Node] = {
      Try(Integer.decode(n \ "@id")).fold(
        (_) => n,
        (keyCode) => {
          keys.get(keyCode).fold(n)(count => {
            n.asInstanceOf[Elem].copy(
              attributes =
                for (attr <- n.attributes) yield attr match {
                  case attr@Attribute("style", _, _) =>
                    attr.copyData(value=s"fill:${color(keyCode.toInt, count).toRGB};fill-opacity:1;stroke:#202326;stroke-width:0")
                  case other => other
                }
            )
          })
        }
      )
    }
  }


}

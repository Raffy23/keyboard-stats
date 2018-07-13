package his.util

import javafx.scene.paint.Color
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
class HeatmapGenerator(keymap: Seq[Node], keys: TrieMap[Int, Long], startColor: Color = Color.WHITE, endColor: Color = Color.RED) {

  implicit class DoubleColorValue(d: Double) {
    def toIntColor: Int = (d * 255).toInt
    def toColorString: String = d.toIntColor.toHexString.toUpperCase
  }

  implicit class HexColor(color: Color) {
    def toHexString: String = s"#${color.getRed.toColorString}${color.getGreen.toColorString}${color.getBlue.toColorString}FF"
    def toRGB: String = s"rgb(${color.getRed.toIntColor},${color.getGreen.toIntColor},${color.getBlue.toIntColor})"
  }

  def transform(): Seq[Node] = new RuleTransformer(xmlColoringRule(max)).transform(keymap)

  def transform(engine: WebEngine): Unit = {
    keys.foreach { case (keyCode, count) => {
      val color = startColor.interpolate(endColor, count.asInstanceOf[Double] / max)
      engine.getDocument.getElementById(s"0x${keyCode.toHexString.toUpperCase}").setAttribute("style", s"fill:${color.toRGB};fill-opacity:1;stroke:#202326;stroke-width:0")
    }}
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
        (id) => {
          keys.get(id).fold(n)(count => {
            val color = startColor.interpolate(endColor, count.asInstanceOf[Double] / max)

            n.asInstanceOf[Elem].copy(
              attributes =
                for (attr <- n.attributes) yield attr match {
                  case attr@Attribute("style", _, _) =>
                    attr.copyData(value=s"fill:${color.toRGB};fill-opacity:1;stroke:#202326;stroke-width:0")
                  case other => other
                }
            )
          })
        }
      )
    }
  }


}

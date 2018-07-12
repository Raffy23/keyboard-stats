package his.service

import java.io.InputStreamReader

import scala.io.Source
import scala.xml.{Node, XML}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 12.07.2018
  */
object KeyboardLayoutService {

  private def fromResource(name: String): InputStreamReader =
    Source.fromResource(name, KeyboardLayoutService.getClass.getClassLoader).reader()

  val layouts: Map[String, Seq[Node]] = Map(
    "qwerty" -> XML.load(fromResource("Qwerty.svg"))
  )

}

package his.service

import java.io.{ByteArrayOutputStream, InputStreamReader, OutputStreamWriter}

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

  def layoutToString(name: String): String = {
    val byteOut = new ByteArrayOutputStream()
    val byteWriter = new OutputStreamWriter(byteOut)
    XML.write(byteWriter, layouts(name).head, "UTF-8", xmlDecl = false, null)
    byteWriter.flush()
    byteWriter.close()

    new String(byteOut.toByteArray)
  }

}

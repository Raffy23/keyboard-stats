package his.util

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, OutputStreamWriter}

import javax.imageio.ImageIO
import org.apache.batik.transcoder.TranscoderInput

import scala.xml.{Node, XML}

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
object SVGUtility {

  def saveAs(file: String, format: String, svg: Node, width: Int, height: Int): Unit = {
    val byteOut = new ByteArrayOutputStream()
    val byteWriter = new OutputStreamWriter(byteOut)

    XML.write(byteWriter, svg, "UTF-8", xmlDecl = false, null)
    byteWriter.flush()
    byteWriter.close()

    val byteIn = new ByteArrayInputStream(byteOut.toByteArray)
    val t = new BufferedImageTranscoder(width, height)
    val in = new TranscoderInput(byteIn)

    t.transcode(in, null)

    val image = t.getImage
    ImageIO.write(image, format, new File(file + "." + format))
  }

}

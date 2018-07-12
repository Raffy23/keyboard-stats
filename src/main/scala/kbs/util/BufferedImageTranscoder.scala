package kbs.util

import java.awt.image.BufferedImage

import org.apache.batik.transcoder.{TranscoderException, TranscoderOutput}
import org.apache.batik.transcoder.image.ImageTranscoder

/**
  * Created by: 
  *
  * @author Raphael
  * @version 12.07.2018
  */
class BufferedImageTranscoder(width: Float, height: Float) extends ImageTranscoder {
  import org.apache.batik.transcoder.SVGAbstractTranscoder._
  private var image: BufferedImage = _

  addTranscodingHint(KEY_WIDTH, width)
  addTranscodingHint(KEY_HEIGHT, height)

  override def createImage(width: Int, height: Int) = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

  @throws[TranscoderException]
  override def writeImage(img: BufferedImage, output: TranscoderOutput): Unit = {
    this.image = img
  }

  def getImage: BufferedImage = this.image
}
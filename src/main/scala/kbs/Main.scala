package kbs

import java.io._

import javax.imageio.ImageIO
import kbs.util.KeyEventListener.KEY_PRESSED
import kbs.util.win32.Win32WindowUtils
import kbs.util.{BufferedImageTranscoder, GlobalKeyListener, HeatmapGenerator}
import org.apache.batik.transcoder.TranscoderInput

import scala.collection.concurrent.TrieMap
import scala.io.Source
import scala.language.implicitConversions
import scala.xml._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 10.07.2018
  */
object Main extends App {

  // Global storage of Data
  val apps = new TrieMap[String, TrieMap[Int, Long]]()

  // Register input hooks
  val globalKeyListener = new GlobalKeyListener((event, keyCode) => {
    if (event == KEY_PRESSED)
      apps.synchronized {
        val appName = Win32WindowUtils.getActiveProcessName
        val app     = apps.getOrElseUpdate(appName, new TrieMap[Int, Long])

        app.put(keyCode, app.getOrElse(keyCode, 0L) + 1L)
      }
  })

  // Load keymap
  val qwerty = XML.load(Source.fromResource("Qwerty.svg", Main.getClass.getClassLoader).reader())

  // Start listening
  globalKeyListener.start()

  // Wait for input to start processing
  println("Press ENTER to exit ...")
  System.in.read()

  // Stop listening to keys
  globalKeyListener.destroy()

  // Write Output PNG / SVG Files
  val app = "idea64.exe"
  val output = new HeatmapGenerator(qwerty, apps(app)).transform()
  val byteOut = new ByteArrayOutputStream()
  val byteWriter = new OutputStreamWriter(byteOut)

  println("write xml to buffer")
  XML.write(byteWriter, output.head, "UTF-8", xmlDecl = false, null)
  byteWriter.flush()
  byteWriter.close()

  println("write xml to file")
  XML.save("./output.svg", output.head)


  val byteIn = new ByteArrayInputStream(byteOut.toByteArray)

  val t = new BufferedImageTranscoder(776*2, 236*2)
  val in = new TranscoderInput(byteIn)

  println("transcode to png ...")
  t.transcode(in, null)


  println("save image to file")
  val image = t.getImage
  ImageIO.write(image, "png", new File("./heatmap.png"))

  println("done")
}
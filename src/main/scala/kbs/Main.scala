package kbs

import java.util.concurrent.atomic.AtomicReference

import javafx.scene.input.KeyCode
import kbs.util.GlobalKeyListener
import kbs.util.KeyEventListener.KEY_PRESSED
import kbs.util.win32.{Win32RawInput, Win32WindowUtils}

import scala.collection.mutable

/**
  * Created by: 
  *
  * @author Raphael
  * @version 10.07.2018
  */
object Main extends App {

  val apps = new mutable.HashMap[String, mutable.HashMap[KeyCode, Long]]()
  val globalKeyListener = new GlobalKeyListener((event, keyCode) => {
    if (event == KEY_PRESSED)
      apps.synchronized {
        val appName = Win32WindowUtils.getActiveProcessName
        val app     = apps.getOrElseUpdate(appName, new mutable.HashMap[KeyCode, Long])

        app.put(keyCode, app.getOrElse(keyCode, 0L) + 1L)
      }
  })

  //Tests for the SVG Stuff
  //val qwerty = XML.load(Source.fromResource("Qwerty.svg", Main.getClass.getClassLoader).reader())
  //println(qwerty \\ "_" filter {node => (node \ "@id").text == "text485"})


  globalKeyListener.start()

  println("Press ENTER to exit ...")
  System.in.read()

  globalKeyListener.destroy()
  println(apps)

}
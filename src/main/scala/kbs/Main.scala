package kbs

import java.util.concurrent.atomic.AtomicReference

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

  val apps = new mutable.HashMap[String, mutable.HashMap[Int, Long]]()

  //Tests for the SVG Stuff
  //val qwerty = XML.load(Source.fromResource("Qwerty.svg", Main.getClass.getClassLoader).reader())
  //println(qwerty \\ "_" filter {node => (node \ "@id").text == "text485"})


  val rawInput = new AtomicReference[Win32RawInput](null)
  val rawInputMessageThread = new Thread(() => {
    rawInput.set(new Win32RawInput((event, keyCode) => {
      if (event == KEY_PRESSED)
        apps.synchronized {
          val appName = Win32WindowUtils.getActiveProcessName
          val app     = apps.getOrElseUpdate(appName, new mutable.HashMap[Int, Long])

          app.put(keyCode, app.getOrElse(keyCode, 0L) + 1L)
        }
    }))

    rawInput.get().enterMessageLoop()
  })
  rawInputMessageThread.start()

  System.in.read()
  rawInput.get().destroy()
  rawInputMessageThread.interrupt()

  println(apps)

}
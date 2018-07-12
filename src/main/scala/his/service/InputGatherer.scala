package his.service

import java.util.concurrent.atomic.AtomicLong

import his.util.KeyEventListener.KEY_RELEASED
import his.util.win32.Win32WindowUtils
import his.util.{GlobalKeyListener, KeyEventListener}

import scala.collection.concurrent.TrieMap

/**
  * Created by: 
  *
  * @author Raphael
  * @version 12.07.2018
  */
object InputGatherer {

  val listeners = new java.util.Vector[KeyEventListener]()

  val apps = new TrieMap[String, TrieMap[Int, Long]]()
  val appsMax = new TrieMap[String, Long]()

  val all = new TrieMap[Int, Long]()
  val allMax = new AtomicLong(1L)

  val globalKeyListener = new GlobalKeyListener((event, keyCode) => {
    if (event == KEY_RELEASED) {
      val appName = Win32WindowUtils.getActiveProcessName
      val app = apps.getOrElseUpdate(appName, new TrieMap[Int, Long])

      app.put(keyCode, app.getOrElse(keyCode, 0L) + 1L)
      appsMax.put(appName, appsMax.getOrElse(appName, 0L) + 1L)

      all.put(keyCode, all.getOrElse(keyCode, 0L) + 1L)
      allMax.incrementAndGet()

      listeners.forEach(x => x.eventOccurred(event, keyCode))
    }
  })

}

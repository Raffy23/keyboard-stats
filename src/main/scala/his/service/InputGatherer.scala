package his.service

import java.awt.event.KeyEvent
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

  type App = String
  type KeyRecords = TrieMap[Int, Long]

  val listeners = new java.util.Vector[KeyEventListener]()

  val badApps = new java.util.Vector[App]

  val apps = new TrieMap[App, KeyRecords]()
  val appsMax = new TrieMap[App, Long]()

  val all = new TrieMap[Int, Long]()
  val allMax = new AtomicLong(1L)

  val globalKeyListener = new GlobalKeyListener((event, keyCode, _) => {
    if (event == KEY_RELEASED && KeyEvent.VK_UNDEFINED != keyCode) {
      val appName = Win32WindowUtils.getActiveProcessName
      if (!badApps.contains(appName)) {
        val app = apps.getOrElseUpdate(appName, new KeyRecords)

        app.put(keyCode, app.getOrElse(keyCode, 0L) + 1L)
        appsMax.put(appName, appsMax.getOrElse(appName, 0L) + 1L)

        all.put(keyCode, all.getOrElse(keyCode, 0L) + 1L)
        allMax.incrementAndGet()

        listeners.forEach(x => x.eventOccurred(event, keyCode, appName))
      }
    }
  })

}

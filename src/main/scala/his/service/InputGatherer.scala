package his.service

import java.awt.event.KeyEvent

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
  val excludedApps = new java.util.Vector[App]

  val globalKeyListener = new GlobalKeyListener((event, keyCode, _) => {
    if (event == KEY_RELEASED && KeyEvent.VK_UNDEFINED != keyCode) {
      val appName = Win32WindowUtils.getActiveProcessName

      if (!excludedApps.contains(appName))
        listeners.forEach(x => x.eventOccurred(event, keyCode, appName))

    }
  })

}

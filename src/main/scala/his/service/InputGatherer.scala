package his.service

import his.util.GlobalKeyListener
import his.util.KeyEventListener.KEY_PRESSED
import his.util.win32.Win32WindowUtils

import scala.collection.concurrent.TrieMap

/**
  * Created by: 
  *
  * @author Raphael
  * @version 12.07.2018
  */
object InputGatherer {


  val apps = new TrieMap[String, TrieMap[Int, Long]]()
  val all = new TrieMap[Int, Long]()

  val globalKeyListener = new GlobalKeyListener((event, keyCode) => {
    if (event == KEY_PRESSED)
      apps.synchronized {
        val appName = Win32WindowUtils.getActiveProcessName
        val app     = apps.getOrElseUpdate(appName, new TrieMap[Int, Long])

        app.put(keyCode, app.getOrElse(keyCode, 0L) + 1L)
        all.put(keyCode, all.getOrElse(keyCode, 0L) + 1L)
      }
  })

}

package kbs.util

import com.sun.jna.Platform
import kbs.util.win32.WindowsKeyListener

/**
  * Created by: 
  *
  * @author Raphael
  * @version 11.07.2018
  */
class GlobalKeyListener(l: KeyEventListener) {

  private val keyListener = Platform.getOSType match {
    case Platform.WINDOWS => new WindowsKeyListener(l)
    case _ => throw new RuntimeException("Unable to create Key Listener for the Platform!")
  }

  def start(): Unit = {
    keyListener.start()
  }

  def destroy(): Unit = {
    keyListener.destroy()
  }

}

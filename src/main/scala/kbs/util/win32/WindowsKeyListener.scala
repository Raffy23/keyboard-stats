package kbs.util.win32

import kbs.util.{KeyEventListener, KeyListener}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by: 
  *
  * @author Raphael
  * @version 11.07.2018
  */
class WindowsKeyListener(keyEventListener: KeyEventListener) extends KeyListener(keyEventListener) {
  private lazy val rawInputListener = new Win32RawInput(keyEventListener)

  override def start(): Future[Unit] = Future {
    rawInputListener.enterMessageLoop()
  }

  override def destroy(): Unit = rawInputListener.destroy()
}

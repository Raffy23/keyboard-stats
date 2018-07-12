package his.util

import scala.concurrent.Future

/**
  * Created by: 
  *
  * @author Raphael
  * @version 11.07.2018
  */
abstract class KeyListener(keyEventListener: KeyEventListener) {
  def start(): Future[Unit]
  def destroy(): Unit
}

package his.service


import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.{InetAddress, ServerSocket, Socket}

import scala.util.Try

/**
  * Created by: 
  *
  * @author Raphael
  * @version 17.07.2018
  */
class SingleInstanceService(message: String, actionListener: (String) => Unit) {

  private val PORT   = 46877
  private val socket = Try(Some(new ServerSocket(PORT, 10, InetAddress.getLocalHost))).getOrElse(None)

  socket.foreach(socket => new Thread(() => {
    while (!socket.isClosed) {
      val instance = socket.accept()
      val in = new BufferedReader(new InputStreamReader(instance.getInputStream))

      Try(actionListener(in.readLine())).recover{ case e: Exception => e.printStackTrace() }

      in.close()
      instance.close()
    }
  }).start())

  if (socket.isEmpty) {
    val client = new Socket(InetAddress.getLocalHost, PORT)
    val out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream))

    out.write(message)
    out.newLine()
    out.flush()
    out.close()
    client.close()
  }

  val isFirstInstance: Boolean = socket.isDefined

  def destroy(): Unit = {
    socket.foreach(_.close())
  }

}

package kbs.util.win32

import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.ptr.IntByReference
import kbs.util.win32.jna.JNAPsapi


/**
  * Created by: 
  *
  * @author Raphael
  * @version 10.07.2018
  */
object Win32WindowUtils {
  private lazy val User32 = com.sun.jna.platform.win32.User32.INSTANCE
  private lazy val Kernel32 = com.sun.jna.platform.win32.Kernel32.INSTANCE
  private lazy val Psapi = JNAPsapi.INSTANCE

  import User32._
  import Kernel32._
  import Psapi._

  protected val PROCESS_QUERY_INFORMATION = 0x0400
  protected val PROCESS_VM_READ = 0x0010

  private val MAX_TITLE_LENGTH = 1024

  def getActiveWindowTitle: String = {
      val buffer = new Array[Char](MAX_TITLE_LENGTH)
      GetWindowText(GetForegroundWindow(), buffer, MAX_TITLE_LENGTH)

      buffer.mkString
  }

  def getActiveProcessName: String = {
      val processID = new IntByReference
      GetWindowThreadProcessId(GetForegroundWindow, processID)

      val process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, processID.getValue)
      if (process == null)
        throw new Win32Exception(GetLastError())

      val buffer = new Array[Char](MAX_TITLE_LENGTH)
      GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH)

      buffer.mkString
  }

}

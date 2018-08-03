package keyboardstats.util.win32

import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinDef._
import com.sun.jna.platform.win32.WinUser._
import com.sun.jna.win32.W32APIOptions
import com.sun.jna.{Native, Structure}
import keyboardstats.util.KeyEventListener
import keyboardstats.util.KeyEventListener.{KEY_PRESSED, KEY_RELEASED}
import keyboardstats.util.win32.jna.{RAWINPUT, RAWINPUTDEVICE, RAWINPUTHEADER, User32Lib}

import scala.language.implicitConversions

/**
  * Created by:
  * @author Raphael
  * @version 11.07.2018
  */
class Win32RawInput(keyEventListener: KeyEventListener) {
  // Load native DLLs
  private lazy val user32 = com.sun.jna.platform.win32.User32.INSTANCE
  private lazy val kernel32 = com.sun.jna.platform.win32.Kernel32.INSTANCE
  private lazy val user32Lib = Native.loadLibrary("user32", classOf[User32Lib], W32APIOptions.DEFAULT_OPTIONS)

  import Win32RawInput._

  // Import the functions to have a more C like Code style
  import User32Lib._
  import kernel32._
  import user32._
  import user32Lib._

  private val rid = Array(new RAWINPUTDEVICE)
  private val keyState = Array.ofDim[Byte](256)

  protected val WndProc = new com.sun.jna.Callback {
    private val FALSE = new LRESULT(0)

    private implicit def toUSHORT(int: Int): USHORT = new USHORT(int)
    private implicit def toUINT(long: Long): UINT = new UINT(long)
    private implicit def toDWORD(long: Int): DWORD = new DWORD(long)

    def callback(hWndMain: HWND, uMsg: UINT, wParam: WPARAM, lParam: LPARAM) : LRESULT = uMsg.intValue() match {
      case WM_CREATE =>
        // Build RawInputDevice structure:
        import RAWINPUTDEVICE._
        rid(0).dwFlags      = RIDEV_INPUTSINK // JavaFX does not work with RIDEV_NOLEGACY
        rid(0).usUsagePage  = 1
        rid(0).usUsage      = RID_KEYBOARD
        rid(0).hwndTarget   = hWndMain

        // Register device
        if(!RegisterRawInputDevices(rid, 1, rid(0).size()))
          throw new Win32Exception(GetLastError())

        // Get initial Keyboard state
        GetKeyboardState(keyState)

        FALSE
      case WM_INPUT =>
        val dwSize = new UINTByReference(0L)

        // Query number of events:
        if (GetRawInputData(lParam, RID_INPUT, null, dwSize, sizeof(RAWINPUTHEADER_)) == UINT_MAX)
          throw new Win32Exception(GetLastError())

        // Allocate Memory:
        val raw = new RAWINPUT
        if (dwSize.getValue.intValue() >= sizeof(raw))
          throw new RuntimeException("Error: RAWINPUT Structure is too small for Data (wtf ?)")

        // Query actual events:
        if (GetRawInputData(lParam, RID_INPUT, raw, dwSize, sizeof(RAWINPUTHEADER_)) != dwSize.getValue.longValue())
          throw new Win32Exception(GetLastError())

        raw.header.dwType.intValue() match {
          case RIM_TYPEKEYBOARD =>
            raw.data.readField("keyboard") // <-- wow

            if (raw.data.keyboard.Message.intValue() == WM_KEYUP) {
              keyEventListener.eventOccurred(
                KEY_RELEASED,
                Win32KeyCodeMapper.map(raw.data.keyboard.VKey.intValue()),
                null
              )
            }

          case RIM_TYPEMOUSE => println("Mouse Events are not implemented")
          case _ => println("What kind of event it this ?")
        }

        FALSE
      case WM_CLOSE => PostQuitMessage(0); FALSE
      case _ => DefWindowProc(hWndMain, uMsg.intValue(), wParam, lParam)
    }
  }

  private val wndClass    = new WNDCLASSEX()
  wndClass.lpfnWndProc    = WndProc
  wndClass.hInstance      = GetModuleHandle(null)
  wndClass.lpszClassName  = WINDOW_CLASS_NAME
  RegisterClassEx(wndClass)

  protected val hWnd: HWND = CreateWindowEx(0, WINDOW_CLASS_NAME, null, 0, 0, 0, 0, 0, HWND_MESSAGE, null, wndClass.hInstance, null)
  if (hWnd == null)
    throw new Win32Exception(GetLastError())

  def enterMessageLoop(): Unit = {
    val message = new MSG

    while(GetMessage(message, null, 0, 0) != 0) {
      TranslateMessage(message)
      DispatchMessage(message)

      if (Thread.currentThread().isInterrupted)
        return
    }

  }

  def destroy(): Unit = {
    DestroyWindow(hWnd)
  }

}

object Win32RawInput {

  // Some constances which are not defined anywhere
  val UINT_MAX = 0xffffffffL
  val WM_INPUT = 0x00FF

  val RID_KEYBOARD = 0x06
  val RID_MOUSE = 0x02
  val RID_GAMEPAD = 0x05

  val WINDOW_CLASS_NAME = "Scala_RAW_KEYBOARD_INPUT"

  private lazy val RAWINPUTHEADER_ = new RAWINPUTHEADER()
  protected def sizeof(structure: Structure): Int = structure.size()
}

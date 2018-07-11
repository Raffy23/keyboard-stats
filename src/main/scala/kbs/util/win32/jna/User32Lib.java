package kbs.util.win32.jna;

import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.UINTByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Created by:
 *
 * @author Raphael
 * @version 11.07.2018
 */
public interface User32Lib extends StdCallLibrary {

  int RID_HEADER = 0x10000005;
  int RID_INPUT  = 0x10000003;

  boolean RegisterRawInputDevices(RAWINPUTDEVICE[] pRawInputDevices, int uiNumDevices, int cbSize);
  long GetRawInputData(LPARAM hRawInput, int uiCommand, RAWINPUT pData, UINTByReference pcbSize, int cbSizeHeader);
}

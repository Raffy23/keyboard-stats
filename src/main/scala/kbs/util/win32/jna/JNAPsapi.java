package kbs.util.win32.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Created by:
 *
 * @author Raphael
 * @version 10.07.2018
 */
public interface JNAPsapi extends StdCallLibrary {
  JNAPsapi INSTANCE =  Native.loadLibrary("psapi", JNAPsapi.class);

  int GetModuleBaseNameW(HANDLE hProcess, Pointer hmodule, char[] lpBaseName, int size);
}

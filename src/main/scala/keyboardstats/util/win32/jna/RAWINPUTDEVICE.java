package keyboardstats.util.win32.jna;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.USHORT;
import java.util.Arrays;
import java.util.List;

/**
 * https://msdn.microsoft.com/en-us/library/windows/desktop/ms645565%28v=vs.85%29.aspx?f=255&MSPPError=-2147217396
 *
 * Created by:
 * @author Raphael
 * @version 11.07.2018
 */
public class RAWINPUTDEVICE extends Structure {
  public static final int RIDEV_APPKEYS       = 0x00000400;
  public static final int RIDEV_CAPTUREMOUSE  = 0x00000200;
  public static final int RIDEV_DEVNOTIFY     = 0x00002000;
  public static final int RIDEV_EXCLUDE       = 0x00000010;
  public static final int RIDEV_INPUTSINK     = 0x00000100;
  public static final int RIDEV_NOHOTKEYS     = 0x00000200;
  public static final int RIDEV_NOLEGACY      = 0x00000030;
  public static final int RIDEV_PAGEONLY      = 0x00000020;
  public static final int RIDEV_REMOVE        = 0x00000001;

  public USHORT usUsagePage;
  public USHORT usUsage;
  public DWORD dwFlags;
  public HWND hwndTarget;

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("usUsagePage", "usUsage", "dwFlags", "hwndTarget");
  }
}

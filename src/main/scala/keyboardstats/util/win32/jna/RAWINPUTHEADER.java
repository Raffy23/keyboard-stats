package keyboardstats.util.win32.jna;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import java.util.Arrays;
import java.util.List;

/**
 * Created by:
 *
 * @author Raphael
 * @version 11.07.2018
 */
public class RAWINPUTHEADER extends Structure {
  public DWORD dwType;
  public DWORD dwSize;
  public HANDLE hDevice;
  public WPARAM wParam;

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("dwType", "dwSize", "hDevice", "wParam");
  }
}

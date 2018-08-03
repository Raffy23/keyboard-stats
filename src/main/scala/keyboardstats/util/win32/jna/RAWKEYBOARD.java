package keyboardstats.util.win32.jna;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import java.util.Arrays;
import java.util.List;

/**
 * Created by:
 *
 * @author Raphael
 * @version 11.07.2018
 */
public class RAWKEYBOARD extends Structure {
  public static final int RI_KEY_BREAK = 1;
  public static final int RI_KEY_E0 = 2;
  public static final int RI_KEY_E1 = 4;
  public static final int RI_KEY_MAKE = 0;


  public USHORT MakeCode;
  public USHORT Flags;
  public USHORT Reserved;
  public USHORT VKey;
  public UINT Message;
  public ULONG ExtraInformation;

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("MakeCode", "Flags", "Reserved", "VKey", "Message", "ExtraInformation");
  }
}

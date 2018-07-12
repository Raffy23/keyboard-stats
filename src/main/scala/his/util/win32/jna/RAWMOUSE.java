package his.util.win32.jna;

import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef.LONG;
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
public class RAWMOUSE extends Structure {
  public static class RAWMOUSE_ANON_STRUCT extends Structure {
    public USHORT usButtonFlags;
    public USHORT usButtonData;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("usButtonFlags", "usButtonData");
    }
  }
  public static class RAWMOUSE_ANON_UNION extends Union {
    public ULONG ulButtons;
    public RAWMOUSE_ANON_STRUCT usButton;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("ulButtons", "usButton");
    }
  }

  public USHORT usFlags;
  public RAWMOUSE_ANON_UNION Buttons;

  public ULONG ulRawButtons;
  public LONG lLastX;
  public LONG lLastY;
  public ULONG ulExtraInformation;

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("usFlags", "Buttons", "ulRawButtons", "lLastX", "lLastY", "ulExtraInformation");
  }
}

package his.util.win32.jna;

import com.sun.jna.Structure;
import com.sun.jna.Union;
import java.util.Arrays;
import java.util.List;

/**
 * Created by:
 *
 * @author Raphael
 * @version 11.07.2018
 */
public class RAWINPUT extends Structure {

  public static class RAWINPUT_DATA_UNION extends Union {
    public RAWMOUSE    mouse;
    public RAWKEYBOARD keyboard;
    public RAWHID      hid;

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("mouse", "keyboard", "hid");
    }
  }

  public RAWINPUTHEADER header;
  public RAWINPUT_DATA_UNION data;

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("header", "data");
  }
}

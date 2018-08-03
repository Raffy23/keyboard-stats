package keyboardstats.util.win32.jna;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import java.util.Arrays;
import java.util.List;

/**
 * Created by:
 *
 * @author Raphael
 * @version 11.07.2018
 */
public class RAWHID extends Structure {
  public DWORD dwSizeHid;
  public DWORD dwCount;
  public byte bRawData[] = {0x00};

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("dwSizeHid", "dwCount", "bRawData");
  }
}

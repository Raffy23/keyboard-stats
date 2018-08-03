package keyboardstats.util.win32

import java.awt.event.KeyEvent

/**
  * Created by: 
  *
  * @author Raphael
  * @version 11.07.2018
  */
object Win32KeyCodeMapper {
  import Win32KeyCodeMapper._

  def map(keyCode: Int): Int = keyCode match {
    case VK_BACK => KeyEvent.VK_BACK_SPACE
    case VK_TAB => KeyEvent.VK_TAB
    case VK_CLEAR => KeyEvent.VK_CLEAR
    case VK_RETURN => KeyEvent.VK_ENTER
    case VK_APPS => KeyEvent.VK_CONTEXT_MENU

    case VK_SHIFT => KeyEvent.VK_SHIFT
    case VK_CONTROL => KeyEvent.VK_CONTROL
    case VK_MENU => KeyEvent.VK_ALT
    case VK_PAUSE => KeyEvent.VK_PAUSE
    case VK_CAPITAL => KeyEvent.VK_CAPS_LOCK
    case VK_KANA => KeyEvent.VK_KANA

    case VK_ESCAPE => KeyEvent.VK_ESCAPE
    case VK_CONVERT => KeyEvent.VK_CONVERT
    case VK_NONCONVERT => KeyEvent.VK_NONCONVERT
    case VK_ACCEPT => KeyEvent.VK_ACCEPT
    case VK_MODECHANGE => KeyEvent.VK_MODECHANGE

    case VK_SPACE => KeyEvent.VK_SPACE
    case VK_END => KeyEvent.VK_END
    case VK_HOME => KeyEvent.VK_HOME
    case VK_LEFT => KeyEvent.VK_LEFT
    case VK_UP => KeyEvent.VK_UP
    case VK_RIGHT => KeyEvent.VK_RIGHT
    case VK_DOWN => KeyEvent.VK_DOWN
    case VK_PRINT => KeyEvent.VK_PRINTSCREEN
    case VK_INSERT => KeyEvent.VK_INSERT
    case VK_DELETE => KeyEvent.VK_DELETE
    case VK_HELP => KeyEvent.VK_HELP

    case VK_KEY_0 => KeyEvent.VK_0
    case VK_KEY_1 => KeyEvent.VK_1
    case VK_KEY_2 => KeyEvent.VK_2
    case VK_KEY_3 => KeyEvent.VK_3
    case VK_KEY_4 => KeyEvent.VK_4
    case VK_KEY_5 => KeyEvent.VK_5
    case VK_KEY_6 => KeyEvent.VK_6
    case VK_KEY_7 => KeyEvent.VK_7
    case VK_KEY_8 => KeyEvent.VK_8
    case VK_KEY_9 => KeyEvent.VK_9

    case VK_KEY_A => KeyEvent.VK_A
    case VK_KEY_B => KeyEvent.VK_B
    case VK_KEY_C => KeyEvent.VK_C
    case VK_KEY_D => KeyEvent.VK_D
    case VK_KEY_E => KeyEvent.VK_E
    case VK_KEY_F => KeyEvent.VK_F
    case VK_KEY_G => KeyEvent.VK_G
    case VK_KEY_H => KeyEvent.VK_H
    case VK_KEY_I => KeyEvent.VK_I
    case VK_KEY_J => KeyEvent.VK_J
    case VK_KEY_K => KeyEvent.VK_K
    case VK_KEY_L => KeyEvent.VK_L
    case VK_KEY_M => KeyEvent.VK_M
    case VK_KEY_N => KeyEvent.VK_N
    case VK_KEY_O => KeyEvent.VK_O
    case VK_KEY_P => KeyEvent.VK_P
    case VK_KEY_Q => KeyEvent.VK_Q
    case VK_KEY_R => KeyEvent.VK_R
    case VK_KEY_S => KeyEvent.VK_S
    case VK_KEY_T => KeyEvent.VK_T
    case VK_KEY_U => KeyEvent.VK_U
    case VK_KEY_V => KeyEvent.VK_V
    case VK_KEY_W => KeyEvent.VK_W
    case VK_KEY_X => KeyEvent.VK_X
    case VK_KEY_Y => KeyEvent.VK_Y
    case VK_KEY_Z => KeyEvent.VK_Z

    case VK_LWIN => KeyEvent.VK_WINDOWS
    case VK_RWIN => KeyEvent.VK_WINDOWS

    case VK_NUMPAD0 => KeyEvent.VK_NUMPAD0
    case VK_NUMPAD1 => KeyEvent.VK_NUMPAD1
    case VK_NUMPAD2 => KeyEvent.VK_NUMPAD2
    case VK_NUMPAD3 => KeyEvent.VK_NUMPAD3
    case VK_NUMPAD4 => KeyEvent.VK_NUMPAD4
    case VK_NUMPAD5 => KeyEvent.VK_NUMPAD5
    case VK_NUMPAD6 => KeyEvent.VK_NUMPAD6
    case VK_NUMPAD7 => KeyEvent.VK_NUMPAD7
    case VK_NUMPAD8 => KeyEvent.VK_NUMPAD8
    case VK_NUMPAD9 => KeyEvent.VK_NUMPAD9

    case VK_MULTIPLY => KeyEvent.VK_MULTIPLY
    case VK_ADD => KeyEvent.VK_ADD
    case VK_SEPARATOR => KeyEvent.VK_SEPARATOR
    case VK_SUBTRACT => KeyEvent.VK_SUBTRACT
    case VK_DECIMAL => KeyEvent.VK_DECIMAL
    case VK_DIVIDE => KeyEvent.VK_DIVIDE

    case VK_F1 => KeyEvent.VK_F1
    case VK_F2 => KeyEvent.VK_F2
    case VK_F3 => KeyEvent.VK_F3
    case VK_F4 => KeyEvent.VK_F4
    case VK_F5 => KeyEvent.VK_F5
    case VK_F6 => KeyEvent.VK_F6
    case VK_F7 => KeyEvent.VK_F7
    case VK_F8 => KeyEvent.VK_F8
    case VK_F9 => KeyEvent.VK_F9
    case VK_F10 => KeyEvent.VK_F10
    case VK_F11 => KeyEvent.VK_F11
    case VK_F12 => KeyEvent.VK_F12
    case VK_F13 => KeyEvent.VK_F13
    case VK_F14 => KeyEvent.VK_F14
    case VK_F15 => KeyEvent.VK_F15
    case VK_F16 => KeyEvent.VK_F16
    case VK_F17 => KeyEvent.VK_F17
    case VK_F18 => KeyEvent.VK_F18
    case VK_F19 => KeyEvent.VK_F19
    case VK_F20 => KeyEvent.VK_F20
    case VK_F21 => KeyEvent.VK_F21
    case VK_F22 => KeyEvent.VK_F22
    case VK_F23 => KeyEvent.VK_F23
    case VK_F24 => KeyEvent.VK_F24

    case VK_NUMLOCK => KeyEvent.VK_NUM_LOCK
    case VK_SCROLL => KeyEvent.VK_SCROLL_LOCK
    case VK_LSHIFT => KeyEvent.VK_SHIFT
    case VK_RSHIFT => KeyEvent.VK_SHIFT
    case VK_LCONTROL => KeyEvent.VK_CONTROL
    case VK_RCONTROL => KeyEvent.VK_CONTROL
    case VK_LMENU => KeyEvent.VK_WINDOWS
    case VK_RMENU => KeyEvent.VK_WINDOWS

    case VK_OEM1 => KeyEvent.VK_COLON
    case VK_OEM2 => KeyEvent.VK_SLASH
    case VK_OEM3 => KeyEvent.VK_SEMICOLON
    case VK_OEM4 => KeyEvent.VK_BACK_SLASH
    case VK_OEM5 => KeyEvent.VK_CIRCUMFLEX
    case VK_OEM6 => KeyEvent.VK_BACK_QUOTE
    case VK_OEM7 => KeyEvent.VK_QUOTE
    case VK_OEM102 => KeyEvent.VK_LESS

    case VK_OEM_MINUS => KeyEvent.VK_MINUS
    case VK_OEM_PLUS => KeyEvent.VK_PLUS
    case VK_OEM_COMMA => KeyEvent.VK_COMMA
    case VK_OEM_PERIOD => KeyEvent.VK_PERIOD

    case VK_PRIOR => KeyEvent.VK_PAGE_UP
    case VK_NEXT => KeyEvent.VK_PAGE_DOWN
    case VK_SNAPSHOT => KeyEvent.VK_PRINTSCREEN

    case x => System.err.println(s"WARNING: Unknown KeyCode: '0x${x.toHexString.toUpperCase}'"); KeyEvent.VK_UNDEFINED
  }

  protected object Win32KeyCodeMapper {
    val VK_BACK = 0x08
    val VK_TAB = 0x09

    val VK_CLEAR = 0x0C
    val VK_RETURN = 0x0D

    val VK_SHIFT = 0x10
    val VK_CONTROL = 0x11
    val VK_MENU = 0x12
    val VK_PAUSE = 0x13
    val VK_CAPITAL = 0x14
    val VK_KANA = 0x15
    val VK_HANGUEL = 0x15
    val VK_HANGUL = 0x15

    val VK_ESCAPE = 0x1B
    val VK_CONVERT = 0x1C
    val VK_NONCONVERT = 0x1D
    val VK_ACCEPT = 0x1E
    val VK_MODECHANGE = 0x1F

    val VK_SPACE = 0x20
    val VK_PRIOR = 0x21
    val VK_NEXT = 0x22
    val VK_END = 0x23
    val VK_HOME = 0x24
    val VK_LEFT = 0x25
    val VK_UP = 0x26
    val VK_RIGHT = 0x27
    val VK_DOWN = 0x28
    val VK_SELECT = 0x29
    val VK_PRINT = 0x2A
    val VK_EXECUTE = 0x2B
    val VK_SNAPSHOT = 0x2C
    val VK_INSERT = 0x2D
    val VK_DELETE = 0x2E
    val VK_HELP = 0x2F

    val VK_KEY_0 = 0x30
    val VK_KEY_1 = 0x31
    val VK_KEY_2 = 0x32
    val VK_KEY_3 = 0x33
    val VK_KEY_4 = 0x34
    val VK_KEY_5 = 0x35
    val VK_KEY_6 = 0x36
    val VK_KEY_7 = 0x37
    val VK_KEY_8 = 0x38
    val VK_KEY_9 = 0x39

    val VK_KEY_A = 0x41
    val VK_KEY_B = 0x42
    val VK_KEY_C = 0x43
    val VK_KEY_D = 0x44
    val VK_KEY_E = 0x45
    val VK_KEY_F = 0x46
    val VK_KEY_G = 0x47
    val VK_KEY_H = 0x48
    val VK_KEY_I = 0x49
    val VK_KEY_J = 0x4A
    val VK_KEY_K = 0x4B
    val VK_KEY_L = 0x4C
    val VK_KEY_M = 0x4D
    val VK_KEY_N = 0x4E
    val VK_KEY_O = 0x4F
    val VK_KEY_P = 0x50
    val VK_KEY_Q = 0x51
    val VK_KEY_R = 0x52
    val VK_KEY_S = 0x53
    val VK_KEY_T = 0x54
    val VK_KEY_U = 0x55
    val VK_KEY_V = 0x56
    val VK_KEY_W = 0x57
    val VK_KEY_X = 0x58
    val VK_KEY_Y = 0x59
    val VK_KEY_Z = 0x5A

    val VK_LWIN = 0x5B
    val VK_RWIN = 0x5C
    val VK_APPS = 0x5D

    val VK_SLEEP = 0x5F

    val VK_NUMPAD0 = 0x60
    val VK_NUMPAD1 = 0x61
    val VK_NUMPAD2 = 0x62
    val VK_NUMPAD3 = 0x63
    val VK_NUMPAD4 = 0x64
    val VK_NUMPAD5 = 0x65
    val VK_NUMPAD6 = 0x66
    val VK_NUMPAD7 = 0x67
    val VK_NUMPAD8 = 0x68
    val VK_NUMPAD9 = 0x69

    val VK_MULTIPLY = 0x6A
    val VK_ADD = 0x6B
    val VK_SEPARATOR = 0x6C
    val VK_SUBTRACT = 0x6D
    val VK_DECIMAL = 0x6E
    val VK_DIVIDE = 0x6F

    val VK_F1 = 0x70
    val VK_F2 = 0x71
    val VK_F3 = 0x72
    val VK_F4 = 0x73
    val VK_F5 = 0x74
    val VK_F6 = 0x75
    val VK_F7 = 0x76
    val VK_F8 = 0x77
    val VK_F9 = 0x78
    val VK_F10 = 0x79
    val VK_F11 = 0x7A
    val VK_F12 = 0x7B
    val VK_F13 = 0x7C
    val VK_F14 = 0x7D
    val VK_F15 = 0x7E
    val VK_F16 = 0x7F
    val VK_F17 = 0x80
    val VK_F18 = 0x81
    val VK_F19 = 0x82
    val VK_F20 = 0x83
    val VK_F21 = 0x84
    val VK_F22 = 0x85
    val VK_F23 = 0x86
    val VK_F24 = 0x87

    val VK_NUMLOCK = 0x90
    val VK_SCROLL = 0x91

    val VK_LSHIFT = 0xA0
    val VK_RSHIFT = 0xA1
    val VK_LCONTROL = 0xA2
    val VK_RCONTROL = 0xA3
    val VK_LMENU = 0xA4
    val VK_RMENU = 0xA5

    val VK_OEM1 = 0xBA
    val VK_OEM2 = 0xBF
    val VK_OEM3 = 0xC0
    val VK_OEM4 = 0xDB
    val VK_OEM5 = 0xDC
    val VK_OEM6 = 0xDD
    val VK_OEM7 = 0xDE
    val VK_OEM102 = 0xE2

    val VK_OEM_PLUS	 = 0xBB
    val VK_OEM_COMMA = 0xBC
    val VK_OEM_MINUS = 0xBD
    val VK_OEM_PERIOD = 0xBE
  }

}
